package blue.l955a6.incrementationMonitor.application.integration.backup

import cats.effect.kernel.Async

trait Backup[A] {
  def write[F[_]: Async](value: A): F[Unit]
}
