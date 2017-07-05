package com.akolov.pantarhei

import sbt._
import Keys._

object NotesPlugin extends Plugin {

  override lazy val settings = Seq(commands += makeNotes)

  lazy val makeNotes =
    Command.command("makeReleaseNotes") { (state: State) =>

      val baseDirectory = Project.extract(state).get(Keys.baseDirectory)
      println(s"baseDir=$baseDirectory")
      val git = Git(baseDirectory)
      val remoteUrl = git.remote

      val github = Github(remoteUrl)
      val pullRequests = github.getPullRequests()

      pullRequests.foreach { pr =>
        println (s"[#${pr.number}](${pr.url})")
        val commits = github.getCommits(pr.number)
        commits.foreach { commit =>
          println( s"[${commit.message}](${commit.url})")
        }
      }


      state
    }

}

