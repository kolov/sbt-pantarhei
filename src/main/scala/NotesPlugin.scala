package com.akolov.pantarhei

import sbt._

object NotesPlugin extends AutoPlugin {

  lazy val makeNotesTask = taskKey[Unit]("Create release notes task")

  override def trigger: PluginTrigger = AllRequirements


  override def projectSettings = Seq(makeNotesTask := makeNotes(Keys.baseDirectory.value, Keys.credentials.value))

  def makeNotes(baseDir: File, credentials: Seq[Credentials]) = {
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

