name := "introduction-to-fp-in-scala"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.1",
  "org.specs2" %% "specs2" % "2.4.16" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"
)

resolvers ++= Seq(
  "oss snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
, "oss releases" at "http://oss.sonatype.org/content/repositories/releases"
, "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
)

scalacOptions in ThisBuild ++= Seq(
  "-deprecation"
, "-unchecked"
, "-feature"
, "-language:_"
, "-Ywarn-value-discard"
, "-Xfatal-warnings"
)
