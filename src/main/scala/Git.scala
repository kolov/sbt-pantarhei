package com.akolov.pantarhei

import java.io.File

import com.typesafe.sbt.git.JGit
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

object Git {
  def apply(baseDir: File) = new Git(baseDir)
}

class Git(val root: File) {


  val repo = (new FileRepositoryBuilder).setWorkTree(root).build()

  val jgit: JGit = new JGit(repo)

  def remote() : String = {
    jgit.repo.getConfig.getString("remote", "origin", "url")
  }


}
