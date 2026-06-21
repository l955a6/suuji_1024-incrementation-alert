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
  .aggregate(application, domain, infrastructure, di, entrypoint)
  .settings(
    publish / skip := true
  )

lazy val application = (project in file("application"))
  .settings(commonSettings)
  .settings(
    name := "application",
    libraryDependencies ++= Seq(
      Dependencies.typesafeConfig,
      Dependencies.fs2Core,
      Dependencies.log4Cats
    )
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

lazy val infrastructure = (project in file("infrastructure"))
  .settings(commonSettings)
  .settings(
    name := "infrastructure",
    libraryDependencies ++= Seq(
      Dependencies.airframeUlid,
      Dependencies.sttp
    )
  )
  .dependsOn(application)

lazy val di = (project in file("di"))
  .settings(commonSettings)
  .settings(
    name := "di",
    libraryDependencies ++= Seq(
      Dependencies.airframeDi
    )
  )
  .dependsOn(application, infrastructure)

lazy val entrypoint = (project in file("entrypoint"))
  .settings(commonSettings)
  .settings(
    name := "entrypoint",
    libraryDependencies ++= Seq(
      Dependencies.catsEffect
    )
  )
  .dependsOn(application, di)
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    dockerBaseImage := "eclipse-temurin:25-jre",
    Docker / packageName := "suuji-1024-incrementation-monitor",
    Docker / daemonUserUid := Some("1001"),
    Docker / daemonUser := "suuji-1024-incrementation-monitor"
  )
