import com.akolov.pantarhei.Github
import org.scalatest.WordSpecLike

class GithubIT extends WordSpecLike {

  "The class Github" should {


    "contain a parseUrl method" should {

      "query pull requests" in {
          val gh = Github("git@github.com:kolov/sbt-pantarhei.git")
        val reqs = gh.getPullRequests(2)
        reqs.foreach(println)
      }

      "query commits" in {
          val gh = Github("git@github.com:kolov/sbt-pantarhei.git")
        val reqs = gh.getCommits(1)
        reqs.foreach(println)
      }
    }
  }
}
