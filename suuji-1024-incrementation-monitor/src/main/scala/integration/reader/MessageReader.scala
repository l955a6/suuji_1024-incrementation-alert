package blue.l955a6.incrementationMonitor.integration

import cats.effect.kernel.Async

trait MessageReader {

  // def connect[F[_]: Async, A](pipe: Pipe[F, Message, A]): F[Unit]
  def connect[F[_]: Async](): F[Unit]
}
