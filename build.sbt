name := "introduction-to-fp-in-scala"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scalaz"     %% "scalaz-core"       % "7.1.5"
, "org.scalaz"     %% "scalaz-effect"     % "7.1.5"
, "org.specs2"     %% "specs2-core"       % "3.6.5"  % "test"
, "org.specs2"     %% "specs2-scalacheck" % "3.6.5"  % "test"
, "org.scalacheck" %% "scalacheck"        % "1.12.5" % "test"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
, Resolver.sonatypeRepo("releases")
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

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.7.1")
