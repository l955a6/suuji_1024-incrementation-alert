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
  .aggregate(incrementationAlert)
  .settings(
    publish / skip := true
  )

lazy val incrementationAlert = (project in file("incrementation-alert"))
  .settings(commonSettings)
  .settings(
    name := "incrementation-alert",
    libraryDependencies ++= Seq()
  )
