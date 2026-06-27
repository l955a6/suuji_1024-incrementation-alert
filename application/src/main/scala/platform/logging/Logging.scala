package blue.l955a6.incrementationMonitor.application.platform.logging

import org.typelevel.log4cats.LoggerFactory

trait Logging[F[_]] {
  protected val loggerFactory: LoggerFactory[F]

  private val logger = loggerFactory.getLogger

  protected def info = logger.info

  protected def error = logger.error
}
