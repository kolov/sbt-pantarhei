package com.akolov.pantarhei

import spray.json.{DefaultJsonProtocol, RootJsonFormat}


case class PullRequest(title: String, htmlUrl: String, number: Int, createdAt: String)

case class Commit(sha: String, url: String)

case class CommitInfo( message: String, author: CommitPerson)

case class CommitPerson(name: String, email: String, date: String)

case class CommitRecord(sha: String, htmlUrl: String, commit: CommitInfo)

case class ReleaseRequest(tagName: String, name: String, body: String)

case class ReleaseResponse(id: Int, tagName: String, name: String, body: String)

case class Tag(tagName: String, commit: Commit)

case class CommitterInfo(name: String, email: String, date: String)


object MyJsonProtocol extends DefaultJsonProtocol {

  implicit val pullRequestFormat: RootJsonFormat[PullRequest] = jsonFormat(
    PullRequest,
    "title",
    "html_url",
    "number",
    "created_at")

  implicit val commitPersonFormat: RootJsonFormat[CommitPerson] = jsonFormat(
    CommitPerson,
    "name",
    "email",
    "date"
  )

  implicit val commitInfoFormat: RootJsonFormat[CommitInfo] = jsonFormat(
    CommitInfo,
    "message",
    "author")

  implicit val commitFormat: RootJsonFormat[Commit] = jsonFormat(
    Commit,
    "sha",
    "url")

  implicit val commitRecordFormat: RootJsonFormat[CommitRecord] = jsonFormat(
    CommitRecord,
    "sha",
    "html_url",
    "commit")

  implicit val releaseRequestFormat: RootJsonFormat[ReleaseRequest] = jsonFormat(
    ReleaseRequest,
    "tag_name",
    "name",
    "body"
  )

  implicit val releaseResponseFormat: RootJsonFormat[ReleaseResponse] = jsonFormat(
    ReleaseResponse,
    "id",
    "tag_name",
    "name",
    "body"
  )
  implicit val tagFormat: RootJsonFormat[Tag] = jsonFormat(
    Tag,
    "name",
    "commit"
  )


}