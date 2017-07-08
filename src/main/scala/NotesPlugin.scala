package com.akolov.pantarhei

import sbt._
import complete.DefaultParsers._

object NotesPlugin extends AutoPlugin {

  lazy val makeNotes = inputKey[Unit]("Create release notes task")

  override def trigger: PluginTrigger = AllRequirements


  override def projectSettings = Seq(
    makeNotes := {
      val baseDirectory = Keys.baseDirectory.value
      val credentials = Keys.credentials.value
      val args = spaceDelimited("<arg>").parsed
      makeNotes(baseDirectory, credentials, args)
    })

  def makeNotes(baseDir: File, credentials: Seq[Credentials], args: Seq[String] ) = {
    println(credentials)
    val githubCredentials = credentials.map(Credentials.toDirect).find(c => c.realm.toLowerCase == "github")
      .getOrElse(
        throw new Exception("Can't find github token. Expected credentials with realm=Github " + "and " +
          "password={token}"))

    println(s"baseDir=$baseDir")
    val git = Git(baseDir)
    val remoteUrl = git.remote

    val github = Github(remoteUrl, githubCredentials.passwd)
    val pullRequests = github.getPullRequests()

    pullRequests.foreach { pr =>
      println(s"[#${pr.number}](${pr.htmlUrl})")
      val commits = github.getCommits(pr.number)
      commits.foreach { record =>
        println(s"* [${record.commit.message}](${record.htmlUrl})")
      }
    }
  }


}

