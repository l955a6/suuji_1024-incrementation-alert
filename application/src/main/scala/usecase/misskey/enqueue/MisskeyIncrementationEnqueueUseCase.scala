package blue.l955a6.incrementationMonitor.application.usecase.misskey.enqueue

import blue.l955a6.incrementationMonitor.application.integration.MessageReader

class MisskeyIncrementationEnqueueUseCase[F[_]](
  reader: MessageReader[F]
) {
  def run(): F[Unit] =
    reader.connect()
}
