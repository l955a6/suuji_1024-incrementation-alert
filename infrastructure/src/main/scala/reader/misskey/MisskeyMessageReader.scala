package blue.l955a6.incrementationMonitor.infrastructure.reader.misskey

import blue.l955a6.incrementationMonitor.application.context.misskey.message.value.MessageContent
import blue.l955a6.incrementationMonitor.application.context.misskey.message.value.MessageId
import blue.l955a6.incrementationMonitor.application.context.misskey.user.value.UserHost
import blue.l955a6.incrementationMonitor.application.context.misskey.user.value.UserId
// import blue.l955a6.incrementationAlert.domain.model.Message
import blue.l955a6.incrementationMonitor.application.context.misskey.value.{
  NoteVisibility,
  Timeline
}
import blue.l955a6.incrementationMonitor.application.integration.MessageReader
// import cats.Applicative
import cats.effect.kernel.Async
import cats.syntax.functor.toFunctorOps
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser
// import fs2.Pipe
import sttp.capabilities.fs2.Fs2Streams
import sttp.client4.basicRequest
import sttp.client4.httpclient.fs2.HttpClientFs2Backend
import sttp.client4.impl.fs2.Fs2WebSockets
import sttp.client4.ws.stream.*
import sttp.model.Uri.UriContext
import sttp.ws.WebSocketFrame
import sttp.ws.WebSocketFrame.Text
import wvlet.airframe.ulid.ULID

class MisskeyMessageReader(config: MisskeyMessageReader.Config) extends MessageReader {

  /**
   * MisskeyサーバとのWebSocket通信が確立されたあと最初に送るデータフレーム。
   *
   * MisskeyのStreaming APIでは、最初に接続するchannelをデータフレームで指定することでそのchannelのデータが送られてくる。
   *
   * @see
   *   https://misskey-hub.net/ja/docs/for-developers/api/streaming/#%E3%83%81%E3%83%A3%E3%83%B3%E3%83%8D%E3%83%AB%E3%81%AB%E6%8E%A5%E7%B6%9A%E3%81%99%E3%82%8B
   */
  private val initDataFrame =
    s"""{"type":"connect","body":{"channel":"${config.timeline.chanelName}","id":"${ULID.newULIDString}"}}"""

  // def connect[F[_]: Async, A](pipe: Pipe[F, Message, A]): F[Unit] =
  def connect[F[_]: Async](): F[Unit] =
    HttpClientFs2Backend.resource[F]().use { backend =>
      basicRequest
        .get(uri"wss://${config.host}/streaming")
        .response(
          asWebSocketStreamAlways[Fs2Streams[F]](Fs2Streams[F]) { in =>
            val init = fs2.Stream
              .emit(
                WebSocketFrame.text(initDataFrame)
              )
            init ++ Fs2WebSockets
              .fromTextPipe(WebSocketFrame.text)(in)
              .collect { case Text(payload, _, _) => payload }
              .map(MisskeyMessageReader.decodePayload)
              .debug()
              .drain
          }
        )
        .send(backend)
        .void
    }
}

object MisskeyMessageReader {
  case class Config(
    host: String,
    timeline: Timeline,
    minVisibility: NoteVisibility,
    excludeLocalOnly: Boolean
  )

  private object MessageBody {
    given Decoder[MessageBody] = deriveDecoder
  }

  private case class MessageBody(
    id: MessageId,
    user: User,
    text: MessageContent,
    visibility: NoteVisibility,
    localOnly: Boolean
  )

  private case class User(
    id: UserId,
    host: UserHost
  )

  private object User {
    given Decoder[User] = deriveDecoder
  }

  private def decodePayload(json: String): Either[io.circe.Error, MessageBody] = {
    val outerDecoder = Decoder.instance[MessageBody](
      _.downField("body").downField("body").as[MessageBody]
    )
    parser.decode[MessageBody](json)(using outerDecoder)
  }
}
