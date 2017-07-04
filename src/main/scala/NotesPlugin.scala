import sbt._
import Keys._
object NotesPlugin extends Plugin {

  override lazy val settings = Seq(commands += makeNotes)

  lazy val makeNotes =
    Command.command("hello") { (state: State) =>

      val baseDirectory = Project.extract(state).get(Keys.baseDirectory)
      println(s"baseDir=$baseDirectory")
      val git = Git(baseDirectory)
      println(s"git remote=${git.remote()}")


      state
    }

}

