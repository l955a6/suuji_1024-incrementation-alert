package blue.l955a6.incrementationMonitor.entrypoint

import blue.l955a6.incrementationMonitor.application.usecase.misskey.enqueue.MisskeyIncrementationEnqueueUseCase
import blue.l955a6.incrementationMonitor.di.MisskeyMessageReaderDesign
import cats.effect.IO
import cats.effect.kernel.Async
import cats.effect.unsafe.implicits.global
import org.typelevel.log4cats.LoggerFactory
import org.typelevel.log4cats.slf4j.Slf4jFactory

object Suuji1024IncrementationMonitor {
  given Async[IO] = IO.asyncForIO
  given LoggerFactory[IO] = Slf4jFactory.create[IO]

  def main(args: Array[String]): Unit =
    MisskeyMessageReaderDesign.design.build[MisskeyIncrementationEnqueueUseCase[IO]] { app =>
      val logger = summon[LoggerFactory[IO]].getLogger
      logger.info("Suuji1024IncrementationMonitor を起動します").flatMap(_ => app.run()).unsafeRunSync()
    }
}
