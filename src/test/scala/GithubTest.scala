import org.scalatest.WordSpecLike

class GithubTest extends WordSpecLike {

  "The class Github" should {
    "contain a parseUrl method" should {

      "parse git@ url" in {
        assert(Github.parseUrl("git@github.com:kolov/sbt-pantarhei.git") == ("github.com", "kolov", "sbt-pantarhei"))
      }
    }
  }
}
