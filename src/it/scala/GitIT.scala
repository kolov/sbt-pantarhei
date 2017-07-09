import java.io.File

import com.akolov.pantarhei.Git
import org.scalatest.WordSpecLike

class GitIT extends WordSpecLike {

  "The class Git" should {

    "retrieve last tag" in {
      val git = Git(new File("/Users/assen/projects/sbt-pantarhei"))
      println(s"Last tag ${git.lastTag}"  )
    }
  }
}
