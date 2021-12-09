# Journal and Snapshots

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

This is the minimum required configuration you need to use `akka-persistence-mapdb`. No further configuration is needed to get it running. Be aware that this by default stores data in memory, check out the @ref:[Configuration](configuration.md) page on how to do that.

## Tagging

To support tagging you need to provide an [event adapter](https://doc.akka.io/docs/akka/current/persistence.html#event-adapters) that wraps your payload using the `akka.persistence.journal.Tagged` class.

Implementing the event adapter:

```scala
package docs.mapdb

import akka.persistence.journal.{EventAdapter, EventSeq, Tagged}

final case class TestEvent(name: String, tag: String)

class TestEventAdapter extends EventAdapter {

  override def fromJournal(event: Any, manifest: String): EventSeq = {
    event match {
      case e: TestEvent => EventSeq.single(e)
    }
  }

  override def manifest(event: Any): String = ""

  override def toJournal(event: Any): Any = {
    event match {
      case e: TestEvent   => Tagged(e, Set(e.tag))
    }
  }

}
```

Then in order to use it configure the event adapter in your `application.conf`:

```json
mapdb-journal {
  event-adapters {
    tagging = "docs.mapdb.TaggingEventAdapter"
  }

  event-adapter-bindings {
    "docs.mapdb.TestEvent" = tagging
  }
}
```

