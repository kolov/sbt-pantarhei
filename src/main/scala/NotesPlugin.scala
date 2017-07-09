package com.akolov.pantarhei

import java.text.SimpleDateFormat

import sbt._


object NotesPlugin extends sbt.AutoPlugin {

  lazy val printNotesAfterLatest = taskKey[Unit]("Create release notes af pull requests after the latest tag")
  lazy val printNotesForLatest = taskKey[Unit]("Create release notes af pull requests for the latest tag")

  override def trigger: PluginTrigger = AllRequirements


  override def projectSettings = Seq(
    printNotesAfterLatest := {
      val baseDirectory = Keys.baseDirectory.value
      val credentials = Keys.credentials.value
      val token = extractToken(credentials)
      new NotesPlugin(baseDirectory, token).makeNotes(FutureCommit)
    },
    printNotesForLatest := {
      val baseDirectory = Keys.baseDirectory.value
      val credentials = Keys.credentials.value
      val token = extractToken(credentials)
      new NotesPlugin(baseDirectory, token).makeNotes(LatestComit)
    }
  )

  private def extractToken(credentials: Seq[Credentials]) = {
    credentials.map(Credentials.toDirect).find(c => c.realm.toLowerCase == "github")
      .getOrElse(
        throw new Exception("Can't find github token. Expected credentials with realm=Github " + "and " +
          "password={token}")).passwd
  }


}


class NotesPlugin(baseDir: java.io.File, token: String) {


  val git = Git(baseDir)
  val remoteUrl = git.remote
  val github = Github(remoteUrl, token)


  case class Parameters(lowerBound: Option[Tag], upperBound: Option[Tag], errorMessage: Option[String]) {
    require(lowerBound.isDefined || upperBound.isDefined || errorMessage.isDefined)

    def this(msg: String) = this(None, None, Some(msg))

    def this(lowerBound: Option[Tag], upperBound: Option[Tag]) = this(lowerBound, upperBound, None)
  }

  def makeNotes(target: Target): Unit = {

    val tags = github.tags

    val parameters = target match {
      case LatestComit => {
        tags.length match {
          case 0 => new Parameters("No tags found. Nothing to do")
          case 1 => new Parameters(None, Some(tags.head))
          case _ => new Parameters(Some(tags.tail.head), Some(tags.head))
        }
      }
      case FutureCommit => {
        tags.length match {
          case 0 => new Parameters(None, None)
          case _ => new Parameters(Some(tags.head), None)
        }
      }
    }

    parameters match {
      case Parameters(_, _, Some(msg)) => {
        println(msg)
        return ()
      }
      case Parameters(lower, upper, _) => {
        printNotes(lower, upper)
      }
    }
  }

  def tagDate(tag: Tag) = github.getCommit(tag.commit.sha).commit.author.date

  def dateStrToTicks(dateStr: String) = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(dateStr).getTime()

  def printNotes(lowerBound: Option[Tag], upperBound: Option[Tag]): Unit = {
    (lowerBound, upperBound) match {
      case (Some(l), Some(u)) => println(s"Preparing release notes from pull requests between tags ${l.tagName} and ${u.tagName}\n")
      case (None, Some(u)) => println(s"Preparing release notes from pull requests before tag ${u.tagName}\n")
      case (Some(l), None) => println(s"Preparing release notes from pull requests after tag ${l.tagName}\n")
      case (None, None) => throw new Exception("Unexpected")
    }

    val upCommit = upperBound.map(t => github.getCommit(t.commit.sha))

    val lowerDate = lowerBound.map(tagDate).map(dateStrToTicks)
    val upperDate = upperBound.map(tagDate).map(dateStrToTicks)
    val sinceStr = lowerBound.map(tagDate)
    val pullRequests = github.pullRequests(since = sinceStr).filter { pr =>
      lowerDate.map(t => t <= dateStrToTicks(pr.createdAt)).getOrElse(true) &&
        upperDate.map(t => t >= dateStrToTicks(pr.createdAt)).getOrElse(true)
    }


    if (pullRequests.isEmpty) {
      println("No pull requests were found since last tag")
    }

    pullRequests.foreach { pr =>
      println(s"[#${pr.number}](${pr.htmlUrl})")
      val commits = github.getPRCommits(pr.number)
      commits.foreach { record =>
        println(s"* [${record.commit.message}](${record.htmlUrl})")
      }
    }
  }
}

class Target

case object LatestComit extends Target

case object FutureCommit extends Target



