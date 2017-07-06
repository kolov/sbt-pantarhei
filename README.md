# sbt-pantarhei [![CircleCI](https://circleci.com/gh/kolov/sbt-pantarhei/tree/master.svg?style=svg)](https://circleci.com/gh/kolov/sbt-pantarhei/tree/master)

Sbt plugin creating release notes for a release from the pull requests and git commits since the latest release.

## Usage

Under development, functionality is very basic but usable. Include the plugin:

    addSbtPlugin("com.akolov" % "sbt-pantarhei" % "0.0.2")
    
then run `sbt makeReleaseNotes`. The output is im markdown, ready to be copy/pasted as github release notes:

[#2](https://github.com/kolov/sbt-pantarhei/pull/2)
* [autoPlugin, credentials](https://github.com/kolov/sbt-pantarhei/commit/449a89324b3293db10dcade85a89ed9849b94548)
* [github token from credentials](https://github.com/kolov/sbt-pantarhei/commit/d990f551fcc2f23f53a677741bd162dd509277f0)
[#1](https://github.com/kolov/sbt-pantarhei/pull/1)
* [parses github remote url](https://github.com/kolov/sbt-pantarhei/commit/e1e35f924ae7b242c92670fd0676063fe7b96423)

To access the Github API, a an access token is needed. It must be defines as sbt Credentials, with `realm=github` and
`password={token}`.
 
## To test

    sbt scripted
    
## Release to Nexus

   sbt publishSigned
    

