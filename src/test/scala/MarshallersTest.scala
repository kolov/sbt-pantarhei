import com.akolov.pantarhei.CommitRecord
import com.akolov.pantarhei.MyJsonProtocol._
import org.scalatest.WordSpecLike
import spray.json._

class MarshallersTest extends WordSpecLike {

  def slurp(resource: String): String =
    scala.io.Source.fromInputStream(getClass.getResourceAsStream(resource)).mkString


  "The Marshallers" should {

    "parse CommitRecord" in {
      val commitRecord = slurp("/commit-record.json").parseJson.convertTo[CommitRecord]
      assert(commitRecord.commit.message == "readme")
    }

    "parse PullRequestsResponse" in {
      val prs = slurp("/get-pull-requests.json")
        .parseJson.asInstanceOf[JsArray].elements.map(e => pullRequestFormat.read(e))
      assert(prs.head.title == "new-feature")
      assert(prs.head.createdAt == "2011-01-26T19:01:12Z")
    }

  }
}
