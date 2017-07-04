sbtPlugin := true

name := "sbt-pantarhei"
organization := "com.akolov"
version := "0.0.1-SNAPSHOT"
description := "sbt plugin to generate releasenotes from github pull requests"
licenses := Seq("MIT License" -> url("https://github.com/kolov/sbt-pantarhei/blob/master/LICENSE"))

addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")
scalacOptions := Seq("-deprecation", "-unchecked")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishArtifact in(Compile, packageBin) := true

publishArtifact in(Test, packageBin) := false

publishArtifact in(Compile, packageDoc) := false

publishArtifact in(Compile, packageSrc) := false

resolvers += "Maven.org" at "http://repo1.maven.org/maven2"


publishMavenStyle := false

publishTo := {
  if (version.value contains "-SNAPSHOT") Some(Resolver.sbtPluginRepo("snapshots"))
  else Some(Resolver.sbtPluginRepo("releases"))
}

credentials += Credentials(Path.userHome / ".ivy2" / ".sbtcredentials")

libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.2.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % "test"

