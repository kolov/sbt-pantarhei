package com.akolov.pantarhei

import spray.json.{DefaultJsonProtocol, RootJsonFormat}


case class PullRequest(title: String, url: String, number: Int)

case class Commit(message: String, url: String)

case class CommitRecord(commit: Commit)

object MyJsonProtocol extends DefaultJsonProtocol {

  implicit val pullRequestFormat: RootJsonFormat[PullRequest] = jsonFormat(
    PullRequest,
    "title",
    "url",
    "number")

  implicit val commitFormat: RootJsonFormat[Commit] = jsonFormat(
    Commit,
    "message",
    "url")

  implicit val commitRecordFormat: RootJsonFormat[CommitRecord] = jsonFormat(
    CommitRecord,
    "commit")

}