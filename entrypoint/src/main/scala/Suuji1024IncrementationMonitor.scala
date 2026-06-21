package blue.l955a6.incrementationMonitor.entrypoint

import blue.l955a6.incrementationMonitor.application.usecase.misskey.enqueue.MisskeyIncrementationEnqueueUseCase
import blue.l955a6.incrementationMonitor.di.MisskeyMessageReaderDesign
import cats.effect.IO
import cats.effect.kernel.Async
import cats.effect.unsafe.implicits.global
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Suuji1024IncrementationMonitor {
  given Async[IO] = IO.asyncForIO
  given Logger[IO] = Slf4jLogger.getLogger[IO]

  def main(args: Array[String]): Unit =
    MisskeyMessageReaderDesign.design.build[MisskeyIncrementationEnqueueUseCase] { app =>
      app.run().unsafeRunSync()
    }
}
