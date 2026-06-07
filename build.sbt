ThisBuild / organization := "l955a6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.8.3"

lazy val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Werror",
    "-Wunused:all"
  )
)

lazy val root = (project in file("."))
  .aggregate(suuji1024IncrementationMonitor, domain)
  .settings(
    publish / skip := true
  )

lazy val suuji1024IncrementationMonitor = (project in file("suuji-1024-incrementation-monitor"))
  .settings(commonSettings)
  .settings(
    name := "suuji-1024-incrementation-monitor",
    libraryDependencies ++= Seq()
  )
  .dependsOn(domain)

lazy val domain = (project in file("domain"))
  .settings(commonSettings)
  .settings(
    name := "domain",
    libraryDependencies ++= Seq(
      Dependencies.scalatest,
      Dependencies.scalacheck
    )
  )
