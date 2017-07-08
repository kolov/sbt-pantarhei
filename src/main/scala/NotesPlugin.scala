package com.akolov.pantarhei

import com.akolov.pantarhei.NotesPlugin.{Latest, OneBeforeLatest, Target}
import sbt._

class NotesPlugin(baseDir: java.io.File, token: String) {

  val git = Git(baseDir)
  val remoteUrl = git.remote
  val github = Github(remoteUrl, token)


  def makeNotes(target: Target): Unit = {


    val tags = github.tags

    val borderCommit = target match {
      case Latest => {
        tags.length match {
          case 0 => Left("No tags found. Nothing to do")
          case _ => Right(tags.head)
        }
      }
      case OneBeforeLatest => {
        tags.length match {
          case 0 => Left("No tags found. Nothing to do")
          case 1 => Left("Only one tag found. Nothing to do")
          case _ => Right(tags.tail.head)
        }
      }
    }

    borderCommit match {
      case Left(msg) => {
        println(msg)
        return ()
      }
      case Right(tag) => {
        printReport(tag)
      }
    }
  }

  def printReport(tag: Tag): Unit = {
    println(s"Latest commit: ${tag.tagName}")
    val taggedCommit = github.getCommit(tag.commit.sha)
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

object NotesPlugin extends AutoPlugin {

  lazy val printNotesAfterLatest = taskKey[Unit]("Create release notes af pull requests after the latest tag")
  lazy val printNotesForLatest = taskKey[Unit]("Create release notes af pull requests for the latest tag")

  override def trigger: PluginTrigger = AllRequirements

  class Target

  case object Latest extends Target

  case object OneBeforeLatest extends Target

  override def projectSettings = Seq(
    printNotesAfterLatest := {
      val baseDirectory = Keys.baseDirectory.value
      val credentials = Keys.credentials.value
      val token = extractToken(credentials)
      new NotesPlugin(baseDirectory, token).makeNotes(Latest)
    } ,
    printNotesForLatest := {
      val baseDirectory = Keys.baseDirectory.value
      val credentials = Keys.credentials.value
      val token = extractToken(credentials)
      new NotesPlugin(baseDirectory, token).makeNotes(OneBeforeLatest)
    }
  )

  private def extractToken(credentials: Seq[Credentials]) = {
    credentials.map(Credentials.toDirect).find(c => c.realm.toLowerCase == "github")
      .getOrElse(
        throw new Exception("Can't find github token. Expected credentials with realm=Github " + "and " +
          "password={token}")).passwd
  }


}

