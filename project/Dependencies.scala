import sbt._

object Dependencies {
  // Versions
  lazy val scalaTestVersion = "3.2.2"
  // Libraries
  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  val catsEffect = "org.typelevel" %% "cats-effect" % "2.3.1"

}
