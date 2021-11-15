package com.fgrutsch.akka.persistence.mapdb.snapshot

import akka.persistence.snapshot.SnapshotStoreSpec
import com.typesafe.config.ConfigFactory

abstract class MapDbSnapshotStoreSpec(configName: String) extends SnapshotStoreSpec(ConfigFactory.load(configName))

class FileDbSnapshotStoreSpec              extends MapDbSnapshotStoreSpec("file-db.conf")
class FileDbNoTransactionSnapshotStoreSpec extends MapDbSnapshotStoreSpec("file-db-no-transaction.conf")

class TempFileDbSnapshotStoreSpec              extends MapDbSnapshotStoreSpec("temp-file-db.conf")
class TempFileDbNoTransactionSnapshotStoreSpec extends MapDbSnapshotStoreSpec("temp-file-db-no-transaction.conf")

class MemoryDbSnapshotStoreSpec              extends MapDbSnapshotStoreSpec("memory-db.conf")
class MemoryDbNoTransactionSnapshotStoreSpec extends MapDbSnapshotStoreSpec("memory-db-no-transaction.conf")
