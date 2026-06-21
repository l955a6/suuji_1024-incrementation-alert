package blue.l955a6.incrementationMonitor.application.integration

import cats.effect.kernel.Async
import org.typelevel.log4cats.Logger

trait MessageReader {

  // def connect[F[_]: Async, A](pipe: Pipe[F, Message, A]): F[Unit]
  def connect[F[_]: Async: Logger](): F[Unit]
}
