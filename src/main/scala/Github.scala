package com.akolov.pantarhei

import com.akolov.pantarhei.MyJsonProtocol._
import spray.json._

import scalaj.http._

class Github(remoteUrl: String, val token: String) {

  val (host, owner, repository) = Github.parseUrl(remoteUrl)
  val apiUrl = s"https://api.github.com/repos/$owner/$repository"
  val home = System.getProperty("user.home")

  def pullRequests(max: Int = 5, since: Option[String] = None): Seq[PullRequest] = {
    val response: HttpResponse[String] = Http(s"$apiUrl/pulls")
      .param("state", "all")
      .params(since match {
        case None => Map[String, String]()
        case Some(date) => Map("since" -> date)
      })
      .header("Accept", "application/vnd.github.v3+json")
      .header("Authorization", s"token $token")
      .asString

    response.body.parseJson.asInstanceOf[JsArray]
      .elements.take(max).map(e => pullRequestFormat.read(e))
  }

  def getPRCommits(number: Int): Seq[CommitRecord] = {
    val response: HttpResponse[String] = Http(s"$apiUrl/pulls/$number/commits")
      .header("Accept", "application/vnd.github.v3+json")
      .header("Authorization", s"token $token")
      .asString

    response.body.parseJson.asInstanceOf[JsArray]
      .elements.map(e => commitRecordFormat.read(e))
  }

  def getLatestRelease(): Option[ReleaseResponse] = {
    val response: HttpResponse[String] = Http(s"$apiUrl/releases/latest")
      .header("Accept", "application/vnd.github.v3+json")
      .header("Authorization", s"token $token")
      .asString

    if (response.code == 404)
      return None
    else if (response.code == 200)
      return Some(response.body.parseJson.convertTo[ReleaseResponse])
    else throw new Exception(s"Error: $response.body")
  }

  def tags(): Seq[Tag] = {
    val response: HttpResponse[String] = Http(s"$apiUrl/tags")
      .header("Accept", "application/vnd.github.v3+json")
      .header("Authorization", s"token $token")
      .asString

    response.body.parseJson.asInstanceOf[JsArray]
      .elements.map(e => tagFormat.read(e))
  }

  def getCommit(sha: String) = {
    val response: HttpResponse[String] = Http(s"$apiUrl/commits/$sha")
      .header("Accept", "application/vnd.github.v3+json")
      .header("Authorization", s"token $token")
      .asString

    response.body.parseJson.convertTo[CommitRecord]
  }

  def pushReleaseNotes(tag: Tag, name: String, body: String) = {
    val response: HttpResponse[String] = Http(s"$apiUrl/releases")
      .postData(releaseRequestFormat.write(ReleaseRequest(tag.tagName, name, body)).toString)
      .header("Accept", "application/vnd.github.v3+json")
      .header("Authorization", s"token $token")
      .asString

    response.body.parseJson.convertTo[ReleaseResponse]
  }

  def editReleaseNotes(release: ReleaseResponse, body: String) = {
    val response: HttpResponse[String] = Http(s"$apiUrl/releases/${release.id}")
      .postData(releaseRequestFormat.write(ReleaseRequest(release.tagName, release.name, body)).toString)
      .method("PATCH")
      .header("Accept", "application/vnd.github.v3+json")
      .header("Authorization", s"token $token")
      .asString

    response.body.parseJson.convertTo[ReleaseResponse]
  }


}


object Github {

  def apply(remoteUrl: String, token: String) = new Github(remoteUrl, token)

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