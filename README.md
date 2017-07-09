# sbt-pantarhei [![CircleCI](https://circleci.com/gh/kolov/sbt-pantarhei/tree/master.svg?style=svg)](https://circleci.com/gh/kolov/sbt-pantarhei/tree/master)

Sbt plugin creating release notes for a release from the recent 
pull requests and git commits. Read more about release notes on [Github](https://github.com/blog/1547-release-your-software)

## Usage

This plugin is under development, the functionality is very basic but usable. Include the plugin:

    addSbtPlugin("com.akolov" % "sbt-pantarhei" % "0.1.0")

This makes two sbt tasks available: `printNotesForLatest` and `printNotesAfterLatest`:


| Command                   |     Description          |
| --------------------------|-------------|
| `printNotesForLatest`     | Prints release notes usable for the latest remote  tag - that is, form all pull requests _after_ the _previous_ tag, if any,  and _before_ the _last_ tag       |
| `printNotesAfterLatest`   | Prints release notes from the pull requests _after_ the _latest_  tag. These notes will be usable for the _next tag_        |


The output is in markdown, ready to be copy/pasted as github release notes. Example:

```
$ sbt printNotesForLatest
Preparing release notes from pull requests before tag 0.0.1

[#2](https://github.com/kolov/sbt-pantarhei/pull/2)
* [autoPlugin, credentials](https://github.com/kolov/sbt-pantarhei/commit/449a89324b3293db10dcade85a89ed9849b94548)
* [github token from credentials](https://github.com/kolov/sbt-pantarhei/commit/d990f551fcc2f23f53a677741bd162dd509277f0)
[#1](https://github.com/kolov/sbt-pantarhei/pull/1)
* [parses github remote url](https://github.com/kolov/sbt-pantarhei/commit/e1e35f924ae7b242c92670fd0676063fe7b96423)


$ sbt printNotesAfterLatest 
Preparing release notes from pull requests after tag 0.0.1
No pull requests were found since last tag
```

To access the Github API, a an access token is needed. It must be defines as sbt Credentials, with `realm=github` and
`password={token}`.
 
## To test

    sbt scripted
    
## Release to Nexus

   sbt publishSigned
    

