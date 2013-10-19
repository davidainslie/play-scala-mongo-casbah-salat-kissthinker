import sbt._
import Keys._

object ApplicationBuild extends Build {

  val appName         = "play-scala-mongo-casbah-salat-kissthinker"

  val appVersion      = "1.0.0"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "org.mongodb" %% "casbah" % "2.6.3",
    "com.novus" %% "salat" % "1.9.3",
    "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.1" withSources() withJavadoc(),
    "org.scalatest" %% "scalatest" % "2.0.M5b" % "test" withSources() withJavadoc(),
    "org.mockito" % "mockito-all" % "1.9.5" % "test" withSources() withJavadoc()
  )

  val main = play.Project(appName, appVersion, appDependencies).settings()
}