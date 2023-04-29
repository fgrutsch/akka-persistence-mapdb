# akka-persistence-mapdb

[![Maven](https://img.shields.io/maven-central/v/com.fgrutsch/akka-persistence-mapdb_2.13?logo=Apache%20Maven&style=for-the-badge)](https://search.maven.org/search?q=g:%22com.fgrutsch%22%20AND%20a:%22akka-persistence-mapdb_2.13%22)
[![Github Actions CI Workflow](https://img.shields.io/github/actions/workflow/status/fgrutsch/akka-persistence-mapdb/ci.yml?logo=Github&style=for-the-badge)](https://github.com/fgrutsch/akka-persistence-mapdb/actions/workflows/ci.yml?query=branch%3Amain)
[![Codecov](https://img.shields.io/codecov/c/github/fgrutsch/akka-persistence-mapdb/main?logo=Codecov&style=for-the-badge)](https://codecov.io/gh/fgrutsch/akka-persistence-mapdb)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=for-the-badge)](https://opensource.org/licenses/Apache-2.0)

akka-persistence-mapdb is a plugin for [akka-persistence](https://doc.akka.io/docs/akka/current/typed/index-persistence.html) which uses MapDB for storing journal and snapshot messages.

## Getting Started

Add the following dependency to your `build.sbt`:

```scala
libraryDependencies += "com.fgrutsch" %% "akka-persistence-mapdb" % "<latest>"
```

Add the following to your `application.conf` to use akka-persistence-mapdb as the persistence backend:

```
akka {
  persistence {
    journal {
      plugin = "mapdb-journal"
    }
    snapshot-store {
      plugin = "mapdb-snapshot"
    }
  }
}
```

This is the minimum required configuration you need to use `akka-persistence-mapdb`. No further configuration is needed to get it running. Be aware that this by default stores data in memory, check out the full documentation on how to change that.

If you need to query the stored events you can use [Persistence Query](https://doc.akka.io/docs/akka/current/persistence-query.html) to stream them from the journal. All queries are supported:

```scala
import akka.actor.ActorSystem
import akka.persistence.query.PersistenceQuery
import com.fgrutsch.akka.persistence.mapdb.query.scaladsl.MapDbReadJournal

val actorSystem: ActorSystem = ???
val readJournal: MapDbReadJournal = PersistenceQuery(actorSystem).readJournalFor[MapDbReadJournal](MapDbReadJournal.Identifier)
```

## Documentation

For the full documentation please check [this](https://akka-persistence-mapdb.fgrutsch.com) link.

## Credits

The code of this project is heavily inspired and based on the [akka-persistence-jdbc](https://github.com/akka/akka-persistence-jdbc) backend implementation. Go check it out!

## Contributors

* [Fabian Grutsch](https://github.com/fgrutsch)

## License

This code is licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.txt).