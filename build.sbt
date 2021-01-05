import Dependencies._

name := "scala-sbt"

version := "0.1"

scalaVersion := "2.13.4"

lazy val data = project

lazy val core = project
  .dependsOn(data)
  .settings(
    libraryDependencies ++= Seq(
      scalaTest,
      catsEffect
    ))

lazy val app = project.dependsOn(data, core)