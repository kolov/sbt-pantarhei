package com.akolov.pantarhei

import sbt._
import complete.DefaultParsers._

object NotesPlugin extends AutoPlugin {

  lazy val printNotesAfterLatest = inputKey[Unit]("Create release notes af pull requests after latest tag")

  override def trigger: PluginTrigger = AllRequirements


  override def projectSettings = Seq(
    printNotesAfterLatest := {
      val baseDirectory = Keys.baseDirectory.value
      val credentials = Keys.credentials.value
      val args = spaceDelimited("<arg>").parsed

      val githubCredentials = credentials.map(Credentials.toDirect).find(c => c.realm.toLowerCase == "github")
        .getOrElse(
          throw new Exception("Can't find github token. Expected credentials with realm=Github " + "and " +
            "password={token}"))

      makeNotes(baseDirectory, githubCredentials.passwd)
    })

  def makeNotes(baseDir: File, token: String): Unit = {

    val git = Git(baseDir)
    val remoteUrl = git.remote

    val github = Github(remoteUrl, token)

    val tags = github.tags
    if (tags.length == 0) {
      println("No tags found. Nothing to do")
      return ()
    }
    val lastCommit = tags.head
    println(s"Latest commit: ${lastCommit.tagName}")
    val taggedCommit = github.getCommit(lastCommit.commit.sha)
    val dateOfTag = taggedCommit.commit.author.date

    val pullRequests = github.pullRequests(since = Some(dateOfTag))

    pullRequests.foreach { pr =>
      println(s"[#${pr.number}](${pr.htmlUrl})")
      val commits = github.getPRCommits(pr.number)
      commits.foreach { record =>
        println(s"* [${record.commit.message}](${record.htmlUrl})")
      }
    }
  }


}

