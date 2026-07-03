package blue.l955a6.incrementationMonitor.infrastructure.reader.misskey

import blue.l955a6.incrementationMonitor.application.context.misskey.message.dto.Message
import blue.l955a6.incrementationMonitor.application.context.misskey.message.value.MessageContent
import blue.l955a6.incrementationMonitor.application.context.misskey.message.value.MessageId
import blue.l955a6.incrementationMonitor.application.context.misskey.message.value.MessageUrl
import blue.l955a6.incrementationMonitor.application.context.misskey.user.dto.{User => DomainUser}
import blue.l955a6.incrementationMonitor.application.context.misskey.user.value.UserHost
import blue.l955a6.incrementationMonitor.application.context.misskey.user.value.UserId
import blue.l955a6.incrementationMonitor.application.context.misskey.value.NoteVisibility
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser

private[misskey] object MisskeyPayloadDecoder {

  private case class MessageBody(
    id: MessageId,
    user: User,
    text: MessageContent,
    uri: MessageUrl,
    visibility: NoteVisibility,
    localOnly: Boolean
  )

  private object MessageBody {
    given Decoder[MessageBody] = deriveDecoder
  }

  private case class User(
    username: UserId,
    host: Option[UserHost]
  )

  private object User {
    given Decoder[User] = deriveDecoder
  }

  private def toMessage(body: MessageBody, defaultHost: UserHost): Message =
    Message(
      id = body.id,
      content = body.text,
      url = body.uri,
      user = DomainUser(
        id = body.user.username,
        host = body.user.host.getOrElse(defaultHost)
      )
    )

  def decode(json: String, defaultHost: UserHost): Either[io.circe.Error, Message] = {
    val outerDecoder = Decoder.instance[MessageBody](
      _.downField("body").downField("body").as[MessageBody]
    )
    parser.decode[MessageBody](json)(using outerDecoder).map(toMessage(_, defaultHost))
  }
}
