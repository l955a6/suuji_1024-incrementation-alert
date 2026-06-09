import sbt._

object Dependencies {
  val typesafeConfig = "com.typesafe" % "config" % "1.4.9"

  val sttp = "com.softwaremill.sttp.client4" %% "fs2" % "4.0.25"

  val scalatest = "org.scalatest" %% "scalatest" % "3.2.20" % "test"
  val scalacheck = "org.scalatestplus" %% "scalacheck-1-19" % "3.2.20.0" % "test"
}
