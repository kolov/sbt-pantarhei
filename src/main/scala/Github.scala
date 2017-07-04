

class Github(remoteUrl: String) {


  val (host, owner, repository) = Github.parseUrl(remoteUrl)
  // curl -H "Accept: application/vnd.github.v3+json" -H "Authorization: token ..." https://api.github
  // .com/repos/tntdigital/lib-bat-common/pulls -D -
}


object Github {
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