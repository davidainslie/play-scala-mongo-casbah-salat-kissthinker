import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play-scala-mongo-casbah-salat-kissthinker"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
	"org.specs2" %% "specs2" % "1.12.3" % "test" withSources() withJavadoc(),
	"org.mongodb" %% "casbah" % "2.6.0",
    "com.novus" %% "salat-core" % "1.9.2-SNAPSHOT"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
	resolvers ++= Seq(
      "OSS Sonatype" at "http://oss.sonatype.org/content/repositories/releases/",
      "OSS Sonatype Snaps" at "http://oss.sonatype.org/content/repositories/snapshots/"
	)
  )
}