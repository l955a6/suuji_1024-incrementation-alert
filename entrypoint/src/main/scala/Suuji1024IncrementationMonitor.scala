package blue.l955a6.incrementationMonitor.entrypoint

import blue.l955a6.incrementationMonitor.di.MisskeyMessageReaderDesign
import blue.l955a6.incrementationMonitor.application.usecase.misskey.enqueue.MisskeyIncrementationEnqueueUseCase
import cats.effect.IO
import cats.effect.kernel.Async
import cats.effect.unsafe.implicits.global

object Suuji1024IncrementationMonitor {
  given Async[IO] = IO.asyncForIO

  def main(args: Array[String]): Unit =
    MisskeyMessageReaderDesign.design.build[MisskeyIncrementationEnqueueUseCase] { app =>
      app.run().unsafeRunSync()
    }
}
