name := "introduction-to-fp-in-scala"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.0",
  "org.specs2" %% "specs2" % "2.4.9" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.6" % "test"
)

resolvers ++= Seq(
  "oss snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "oss releases" at "http://oss.sonatype.org/content/repositories/releases"
)

scalacOptions in ThisBuild ++= Seq(
  "-deprecation"
, "-unchecked"
, "-feature"
, "-language:_"
, "-Ywarn-value-discard"
, "-Xfatal-warnings"
)
