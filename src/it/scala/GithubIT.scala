import java.io.File

import com.akolov.pantarhei.NotesPlugin.Latest
import com.akolov.pantarhei.{Github, NotesPlugin}
import org.scalatest.WordSpecLike

import scala.io.Source

class GithubIT extends WordSpecLike {


  val PW = "password="
  val token = Source.fromFile(s"/Users/assen/.github/token")
    .getLines()
    .toList
    .find {
      _.startsWith(PW)
    }
    .map {
      _.substring(PW.length)
    }
    .head
  val remote = "git@github.com:kolov/sbt-pantarhei.git"

  "The Notes Plugin" should {
    "make RN" in {
      new NotesPlugin(new File("/Users/assen/projects/sbt-pantarhei"), token).makeNotes(Latest)
    }
  }
  "The class Github" should {

    "contain a parseUrl method" should {

      "query pull requests" in {
        val gh = Github(remote, token)
        val reqs = gh.pullRequests(2)
        reqs.foreach(println)
      }

      "query commits" in {
        val gh = Github(remote, token)
        val reqs = gh.getPRCommits(1)
        reqs.foreach(println)
      }

      "query releases" in {
        val gh = Github(remote, token)
        val release = gh.getLatestRelease()
        println(s"latest release: $release")
      }

      "query tags" in {
        val gh = Github(remote, token)
        val tags = gh.tags()
        println(s"tags: ")
        tags.foreach(println)
      }
    }
  }
}
