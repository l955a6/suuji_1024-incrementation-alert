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
  .aggregate(suuji1024IncrementationMonitor, domain, infrastructure, di)
  .settings(
    publish / skip := true
  )

lazy val suuji1024IncrementationMonitor = (project in file("suuji-1024-incrementation-monitor"))
  .settings(commonSettings)
  .settings(
    name := "suuji-1024-incrementation-monitor",
    libraryDependencies ++= Seq(
      Dependencies.typesafeConfig,
      Dependencies.airframeDi,
      Dependencies.airframeUlid,
      Dependencies.fs2Core,
      Dependencies.sttp,
      Dependencies.scalatest
    )
  )
  .dependsOn(domain)
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    dockerBaseImage := "eclipse-temurin:25-jre",
    Docker / daemonUserUid := Some("1001"),
    Docker / daemonUser := "suuji-1024-incrementation-monitor"
  )

lazy val domain = (project in file("domain"))
  .settings(commonSettings)
  .settings(
    name := "domain",
    libraryDependencies ++= Seq(
      Dependencies.scalatest,
      Dependencies.scalacheck
    )
  )

lazy val infrastructure = (project in file("infrastructure"))
  .settings(commonSettings)
  .settings(
    name := "infrastructure",
    libraryDependencies ++= Seq(
      Dependencies.airframeUlid,
      Dependencies.sttp
    )
  )
  .dependsOn(suuji1024IncrementationMonitor)

lazy val di = (project in file("di"))
  .settings(commonSettings)
  .settings(
    name := "di",
    libraryDependencies ++= Seq(
      Dependencies.airframeDi
    )
  )
