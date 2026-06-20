package blue.l955a6.incrementationMonitor.application.usecase.misskey.enqueue

import blue.l955a6.incrementationMonitor.application.integration.MessageReader
import cats.effect.kernel.Async

class MisskeyIncrementationEnqueueUseCase(
  reader: MessageReader
) {
  def run[F[_]: Async](): F[Unit] =
    reader.connect()
}
