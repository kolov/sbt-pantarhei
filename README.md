# sbt-pantarhei [![CircleCI](https://circleci.com/gh/kolov/sbt-pantarhei/tree/master.svg?style=svg)](https://circleci.com/gh/kolov/sbt-pantarhei/tree/master)

Sbt plugin creating release notes for a release from the pull requests and git commits since the latest release.

## Usage

Under development, functionality is very basic but usable. Include the plugin:

    addSbtPlugin("com.akolov" % "sbt-pantarhei" % "0.0.2")
    
then run `sbt makeReleaseNotes`. The output is im markdown, ready to be copy/pasted as github release notes:

    [#2](https://github.com/kolov/sbt-pantarhei/pull/3)
    * [output in markdwon](https://api.github .com/repos/kolov/sbt-pantarhei/git/commits/e1e35f924ae7b242c92670fd0676063fe7b96423)
    * [something else](https://api.github.com/repos/kolov/sbt-pantarhei/git/commits/e1e35f924ae7b242c92670fd0676063fe7b96423)
    [#1](https://github.com/kolov/sbt-pantarhei/pull/1)
    * [parses github remote url](https://api.github.com/repos/kolov/sbt-pantarhei/git/commits/e1e35f924ae7b242c92670fd0676063fe7b96423)

To access the Github API, a an access token is needed. At the moment, the location of the fle containing the token is hard-coded as `~/.github/token`. See https://github.com/settings/tokens.
 
## To test

    sbt scripted
    
## Release to Nexus

   sbt publishSigned
    

