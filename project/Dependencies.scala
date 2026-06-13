import sbt._

object Dependencies {
  val typesafeConfig = "com.typesafe" % "config" % "1.4.9"

  private val airframeVersion = "23.5.6"
  val airframeDi = "org.wvlet.airframe" %% "airframe" % airframeVersion
  val airframeUlid = "org.wvlet.airframe" %% "airframe-ulid" % airframeVersion

  val fs2Core = "co.fs2" %% "fs2-core" % "3.13.0"

  val sttp = "com.softwaremill.sttp.client4" %% "fs2" % "4.0.25"

  val scalatest = "org.scalatest" %% "scalatest" % "3.2.20" % "test"
  val scalacheck = "org.scalatestplus" %% "scalacheck-1-19" % "3.2.20.0" % "test"
}
