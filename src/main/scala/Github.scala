
import scala.io.Source
import scalaj.http._


class Github(remoteUrl: String) {


  val (host, owner, repository) = Github.parseUrl(remoteUrl)
  val apiUrl = s"https://api.github.com/repos/$owner/$repository"
  val home = System.getProperty("user.home")
  val token = Source.fromFile(s"$home/.github/token").getLines().next()

  def getPullRequests(): Unit = {
    val response: HttpResponse[String] = Http(s"$apiUrl/pulls")
      .header("Accept", "application/vnd.github.v3+json")
      .header("Authorization", s"token $token")
      .asString
    println(response)
    println(response.body)
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