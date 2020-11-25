import Dependencies._

name := "scala-sbt"

version := "0.1"

scalaVersion := "2.13.3"

lazy val data = project

lazy val core = project
  .dependsOn(data)
  .settings(
    libraryDependencies += scalaTest
  )

lazy val app = project.dependsOn(data, core)