package com.akolov.pantarhei

import org.scalatest.WordSpecLike

class GithubTest extends WordSpecLike {

  "The class Github" should {
    "contain a parseUrl method" should {

      "parse git@ url" in {
        assert(Github.parseUrl("git@github.com:kolov/sbt-pantarhei.git") == ("github.com", "kolov", "sbt-pantarhei"))
      }
    }

    "contain a parseUrl method" should {

      "query github" in {
          val gh = Github("git@github.com:kolov/sbt-pantarhei.git")
        gh.getPullRequests()
      }
    }
  }
}
