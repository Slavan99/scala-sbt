import Dependencies._

name := "scala-sbt"

version := "0.1"

scalaVersion := "2.13.4"

lazy val core = project
  .settings(
    libraryDependencies ++= Seq(
      scalaTest,
      catsEffect
    ))

lazy val akkaVersion = "2.6.10"

lazy val akkaExamples = project
  .in(file("akkaExamples"))
  .settings(
    name := "akkaExamples",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "2.3.1",
      "com.typesafe.akka" % "akka-actor-typed_2.12" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.akka" % "akka-actor-testkit-typed_2.12" % akkaVersion % Test,
    ),
  )