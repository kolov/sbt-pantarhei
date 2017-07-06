sbtPlugin := true

name := "sbt-pantarhei"
organization := "com.akolov"
description := "sbt plugin to generate release notes from github pull requests"
licenses := Seq("MIT License" -> url("https://github.com/kolov/sbt-pantarhei/blob/master/LICENSE"))

homepage := Some(url("http://example.com"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/kolov/sbt-pantarhei"),
    "scm:git@github.com:kolov/sbt-pantarhei.git"
  )
)

developers := List(
  Developer(
    id    = "kolov",
    name  = "Assen Kolov",
    email = "assen.kolov@gmail.com",
    url   = url("http://kolov-it.com")
  )
)

addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3")

scalacOptions := Seq("-deprecation", "-unchecked")

resolvers += "Maven.org" at "http://repo1.maven.org/maven2"
pomIncludeRepository := { _ => false }
publishMavenStyle := true
publishTo := Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
credentials += Credentials(Path.userHome / ".sonatype" / ".credentials")

libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.2.1"
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.3"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % "test"

