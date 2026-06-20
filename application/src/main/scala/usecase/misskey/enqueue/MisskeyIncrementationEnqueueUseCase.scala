package blue.l955a6.incrementationMonitor.usecase.misskey.enqueue

import blue.l955a6.incrementationMonitor.integration.MessageReader
import cats.effect.kernel.Async

class MisskeyIncrementationEnqueueUseCase(
  reader: MessageReader
) {
  def run[F[_]: Async](): F[Unit] =
    reader.connect()
}
