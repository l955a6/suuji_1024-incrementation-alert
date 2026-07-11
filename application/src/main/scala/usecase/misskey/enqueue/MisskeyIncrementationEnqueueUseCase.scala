package blue.l955a6.incrementationMonitor.application.usecase.misskey.enqueue

import blue.l955a6.incrementationMonitor.application.integration.MessageReader
import cats.FlatMap
import cats.syntax.flatMap.*
import org.typelevel.log4cats.LoggerFactory

class MisskeyIncrementationEnqueueUseCase[F[_]: FlatMap: LoggerFactory](
  reader: MessageReader[F]
) {
  private val logger = summon[LoggerFactory[F]].getLogger

  def run(): F[Unit] =
    logger.info("インクリメント監視を開始します") >> reader.connect()
}
