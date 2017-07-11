# Panta rhei [![CircleCI](https://circleci.com/gh/kolov/sbt-pantarhei/tree/master.svg?style=svg)](https://circleci.com/gh/kolov/sbt-pantarhei/tree/master)

_*Πάντα ῥεῖ*_ - _everything flows_, and release notes can help keep track of what has changed.

Pantarhei is a sbt plugin that can create release notes from the  
pull requests and git commits in github. Read more about Github's support for 
releases and release notes [here](https://github.com/blog/1547-release-your-software).

## Usage

Include the plugin:

    addSbtPlugin("com.akolov" % "sbt-pantarhei" % "0.2.1")

This makes the following sbt tasks available:


| Command                   |     Description          |
| --------------------------|-------------|
| `printNotesForLatestTag`     | Prints release notes usable for the latest remote  tag - that is, form all pull requests _after_ the _previous_ tag, if any,  and _before_ the _last_ tag       |
| `pushNotesForLatestTag`     | Creates or updates release notes for the latest remote  tag on github.  `-Dpantarhei.release.name=` will define a release name.   |
| `printNotesForNextTag`   | Prints release notes from the pull requests _after_ the _latest_  tag. These notes will be usable for the _next tag_        |


The output is in markdown, ready to be copy/pasted as github release notes. Example:

```
$ sbt printNotesForLatest
Preparing release notes from pull requests before tag 0.0.1

[PR #2](https://github.com/kolov/sbt-pantarhei/pull/2) Config token
* [autoPlugin, credentials](https://github.com/kolov/sbt-pantarhei/commit/449a89324b3293db10dcade85a89ed9849b94548)
* [github token from credentials](https://github.com/kolov/sbt-pantarhei/commit/d990f551fcc2f23f53a677741bd162dd509277f0)

[PR #1](https://github.com/kolov/sbt-pantarhei/pull/1) test PR to use for retreiving PRs
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
    

