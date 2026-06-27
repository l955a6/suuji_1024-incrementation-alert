package blue.l955a6.incrementationMonitor.entrypoint

import blue.l955a6.incrementationMonitor.application.usecase.misskey.enqueue.MisskeyIncrementationEnqueueUseCase
import blue.l955a6.incrementationMonitor.di.MisskeyMessageReaderDesign
import cats.effect.IO
import cats.effect.kernel.Async
import cats.effect.unsafe.implicits.global
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import wvlet.airframe.*

object Suuji1024IncrementationMonitor {
  def main(args: Array[String]): Unit =
    design
      .build[MisskeyIncrementationEnqueueUseCase[IO]] { app =>
        app.run().unsafeRunSync()
      }

  private val asyncRuntimeDesign =
    newDesign
      .bind[Async[IO]]
      .toInstance(IO.asyncForIO)
      .bind[Logger[IO]]
      .toInstance(Slf4jLogger.getLogger[IO])

  private val applicationDesign =
    Seq[Design](
      MisskeyMessageReaderDesign.design
    ).reduceLeft(_ + _)

  private val design = asyncRuntimeDesign + applicationDesign
}
