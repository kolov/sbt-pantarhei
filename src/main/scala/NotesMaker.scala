package com.akolov.pantarhei

import java.text.SimpleDateFormat

import sbt._


object NotesMaker extends sbt.AutoPlugin {

  lazy val printNotesForNextTag = taskKey[Unit]("Create and print release notes af pull requests after the latest tag")
  lazy val printNotesForLatestTag = taskKey[Unit]("Create and print release notes af pull requests for the latest tag")
  lazy val pushNotesForLatestTag = taskKey[Unit]("Create and push release notes af pull requests for the latest tag")

  override def trigger: PluginTrigger = AllRequirements


  override def projectSettings = Seq(
    printNotesForNextTag := {
      val baseDirectory = Keys.baseDirectory.value
      val credentials = Keys.credentials.value
      processNotes(baseDirectory, credentials, FutureCommit, PrintNotes)
    },
    printNotesForLatestTag := {
      val baseDirectory = Keys.baseDirectory.value
      val credentials = Keys.credentials.value
      processNotes(baseDirectory, credentials, LatestComit, PrintNotes)
    },
    pushNotesForLatestTag := {
      val baseDirectory = Keys.baseDirectory.value
      val credentials = Keys.credentials.value
      processNotes(baseDirectory, credentials, LatestComit, PushNotes)
    }
  )

  def processNotes(baseDirectory: java.io.File, credentials: Seq[Credentials],
                   target: Target, action: Action): Unit = {
    val githubCredentials = credentials.map(Credentials.toDirect).find(c => c.realm.toLowerCase == "github")
      .orElse(
        Credentials.loadCredentials(new File(System.getProperty("user.home") + "/.github/credentials")) match {
          case Left(_) => None
          case Right(dc) => Some(dc)
        })
    if (!githubCredentials.isDefined) {
      println("Can't find github authentication token. Expected credentials with realm=Github " + "and " + "password={token}")
      println("If no  credentials are defined in sbt, /.github/credentials is always searched")
      return ()
    }

    new NotesMaker(baseDirectory, githubCredentials.get.passwd)
      .makeNotes(target, action)
  }
}


class NotesMaker(baseDir: java.io.File, token: String) {



  val git = Git(baseDir)
  val remoteUrl = git.remote
  val github = Github(remoteUrl, token)

  case class Parameters(lowerBound: Option[Tag], upperBound: Option[Tag], errorMessage: Option[String]) {
    require(lowerBound.isDefined || upperBound.isDefined || errorMessage.isDefined)

    def this(msg: String) = this(None, None, Some(msg))

    def this(lowerBound: Option[Tag], upperBound: Option[Tag]) = this(lowerBound, upperBound, None)
  }

  def makeNotes(target: Target, action: Action): Unit = {

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
        printNotes(lower, upper, action)
      }
    }
  }

  def tagDate(tag: Tag) = github.getCommit(tag.commit.sha).commit.author.date

  def dateStrToTicks(dateStr: String) = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(dateStr).getTime()

  def printNotes(lowerBound: Option[Tag], upperBound: Option[Tag], action: Action): Unit = {
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

    val body = pullRequests.foldLeft("") { (txt, pr) =>
      val commits = github.getPRCommits(pr.number)
      txt + s"[PR #${pr.number}](${pr.htmlUrl}) ${pr.title}\n" +
        commits.foldLeft("") { (txt, record) =>
          txt + s"* ${record.commit.message} _[${record.sha.substring(0, 7)}](${record.htmlUrl})_\n"
        } + "\n"
    }

    println(body)

    if (action == PushNotes) {
        github.getLatestRelease().find(_.tagName == upperBound.get.tagName) match {
          case Some(release) => {
            println(s"Patching release ${release.id} for tag ${upperBound.get.tagName}")
            github.editReleaseNotes(release, body)
          }
          case None => {
            println(s"Ceealting release for tag ${upperBound.get.tagName}")
            val nameFromProperty = System.getProperty("pantarhei.release.name")
            val releaseName = if( nameFromProperty != null) nameFromProperty else s"Release ${upperBound.get.tagName}"
            github.pushReleaseNotes(upperBound.get, releaseName, body)
          }
        }
    }

  }
}

sealed trait Target

case object LatestComit extends Target

case object FutureCommit extends Target

sealed trait Action

case object PushNotes extends Action

case object PrintNotes extends Action



