package com.akolov.pantarhei

import spray.json.{DefaultJsonProtocol, RootJsonFormat}


case class PullRequest(title: String, htmlUrl: String, number: Int)

case class Commit(message: String)

case class CommitRecord(htmlUrl: String, commit: Commit)

case class Release(tagName: String, body: String)

object MyJsonProtocol extends DefaultJsonProtocol {

  implicit val pullRequestFormat: RootJsonFormat[PullRequest] = jsonFormat(
    PullRequest,
    "title",
    "html_url",
    "number")

  implicit val commitFormat: RootJsonFormat[Commit] = jsonFormat(
    Commit,
    "message")

  implicit val commitRecordFormat: RootJsonFormat[CommitRecord] = jsonFormat(
    CommitRecord,
    "html_url",
    "commit")

  implicit val releaseFormat: RootJsonFormat[Release] = jsonFormat(
    Release,
    "tag_name",
    "body"
  )

}