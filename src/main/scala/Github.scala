package com.akolov.pantarhei

import org.json4s.DefaultFormats
import spray.json._
import DefaultJsonProtocol._
import MyJsonProtocol._

import scala.io.Source
import scalaj.http._

class Github(remoteUrl: String) {


  val (host, owner, repository) = Github.parseUrl(remoteUrl)
  val apiUrl = s"https://api.github.com/repos/$owner/$repository"
  val home = System.getProperty("user.home")
  val token = Source.fromFile(s"$home/.github/token").getLines().next()


  def getPullRequests(max: Int = 5): Seq[PullRequest] = {
    val response: HttpResponse[String] = Http(s"$apiUrl/pulls")
      .param("state", "all")
      .header("Accept", "application/vnd.github.v3+json")
      .header("Authorization", s"token $token")
      .asString

    response.body.parseJson.asInstanceOf[JsArray]
      .elements.take(max).map(e => pullRequestFormat.read(e))
  }

  def getCommits(number: Int): Seq[Commit] = {
    val response: HttpResponse[String] = Http(s"$apiUrl/pulls/$number/commits")
      .header("Accept", "application/vnd.github.v3+json")
      .header("Authorization", s"token $token")
      .asString

    response.body.parseJson.asInstanceOf[JsArray]
      .elements.map(e => commitRecordFormat.read(e)).map(_.commit)
  }

}


object Github {

  def apply(remoteUrl: String) = new Github(remoteUrl)

  def parseUrl(remoteUrl: String): (String, String, String) = {
    if (remoteUrl.startsWith("git@")) {
      val pattern =
        """git@([\w\.\-_]+):([\w\-]+)/([\w\-]+)\.git""".r
      val matched = pattern.findAllIn(remoteUrl).matchData
      if (matched.hasNext) {
        val n = matched.next()
        if (n.groupCount != 3) {
          throw new Exception(s"error parsing urs $remoteUrl")
        }
        (n.group(1), n.group(2), n.group(3))
      } else
        throw new Exception(s"error parsing url $remoteUrl")
    } else
      throw new Exception(s"Unsupported url $remoteUrl")
  }

}