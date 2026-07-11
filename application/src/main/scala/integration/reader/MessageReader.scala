package blue.l955a6.incrementationMonitor.application.integration

trait MessageReader[F[_]] {

  // def connect[F[_]: Async, A](pipe: Pipe[F, Message, A]): F[Unit]
  def connect(): F[Unit]
}
