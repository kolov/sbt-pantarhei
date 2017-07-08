import com.akolov.pantarhei.Github
import org.scalatest.WordSpecLike

import scala.io.Source

class GithubIT extends WordSpecLike {

  "The class Github" should {


    val PW = "password="
    val token = Source.fromFile(s"/Users/assen/.github/token")
      .getLines()
      .toList
      .find { _.startsWith(PW)}
      .map { _.substring(PW.length) }
      .head
    val remote = "git@github.com:kolov/sbt-pantarhei.git"

    "contain a parseUrl method" should {

      "query pull requests" in {
        val gh = Github(remote, token)
        val reqs = gh.getPullRequests(2)
        reqs.foreach(println)
      }

      "query commits" in {
        val gh = Github(remote, token)
        val reqs = gh.getCommits(1)
        reqs.foreach(println)
      }

      "query releases" in {
        val gh = Github(remote, token)
        val release = gh.getLatestRelease()
        println(s"latest release: $release")
      }
    }
  }
}
