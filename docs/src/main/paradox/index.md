# Akka Persistence MapDB

@@@ index

* [Journal and Snapshots](journal-snapshots.md)
* [Query](query.md)
* [Configuration](configuration.md)

@@@

akka-persistence-mapdb is a plugin for [akka-persistence](https://doc.akka.io/docs/akka/current/typed/index-persistence.html) which uses MapDB for storing journal and snapshot messages.

## Installation

Add the following dependency to your `build.sbt`:


@@dependency[sbt,Maven] {
  group="com.fgrutsch"
  artifact="akka-persistence-mapdb_$scala.binary.version$"
  version="$version$"
}

## Changelog

For the changelog check [this](https://github.com/fgrutsch/akka-persistence-mapdb/releases) page.


## Pages

@@ toc