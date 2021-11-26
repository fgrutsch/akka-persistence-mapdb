package com.fgrutsch.akka.persistence.mapdb.util

import akka.persistence.{PersistentRepr, SnapshotMetadata}
import akka.serialization.SerializationExtension
import com.fgrutsch.akka.persistence.mapdb.journal.JournalRow
import com.fgrutsch.akka.persistence.mapdb.snapshot.SnapshotRow
import com.fgrutsch.akka.persistence.mapdb.util.AkkaSerializationSpec.TestMessage
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.TryValues
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import testing.{AkkaSerializable, TestActorSystem}

import java.time.Instant

object AkkaSerializationSpec {
  final case class TestMessage(name: String, birthYear: Int) extends AkkaSerializable
}

class AkkaSerializationSpec extends AnyFunSuite with Matchers with TryValues with TestActorSystem {

  private val serialization = SerializationExtension(actorSystem)

  test("serialize encodes a journal message") {
    val msg    = TestMessage("mapdb", 2021)
    val result = AkkaSerialization.serialize(serialization)(msg)

    val actual = result.success.value
    actual.serId mustBe 33
    actual.serManifest mustBe "com.fgrutsch.akka.persistence.mapdb.util.AkkaSerializationSpec$TestMessage"
    actual.payload must not be empty
  }

  test("deserialize decodes a journal message") {
    val msg           = TestMessage("mapdb", 2021)
    val msgSerialized = AkkaSerialization.serialize(serialization)(msg)

    val row = JournalRow(
      1,
      deleted = false,
      "pid",
      1,
      "writer",
      Instant.EPOCH.toEpochMilli,
      "TestMessage",
      msgSerialized.success.value.payload,
      msgSerialized.success.value.serId,
      msgSerialized.success.value.serManifest,
      Set.empty
    )

    val result           = AkkaSerialization.fromJournalRow(serialization)(row)
    val (repr, ordering) = result.success.value
    repr mustBe PersistentRepr(msg, 1, "pid", writerUuid = "writer")
    ordering mustBe 1
  }

  test("serialize encodes a snapshot message") {
    val msg    = TestMessage("mapdb", 2021)
    val result = AkkaSerialization.serialize(serialization)(msg)

    val actual = result.success.value
    actual.serId mustBe 33
    actual.serManifest mustBe "com.fgrutsch.akka.persistence.mapdb.util.AkkaSerializationSpec$TestMessage"
    actual.payload must not be empty
  }

  test("deserialize decodes a snapshot message") {
    val msg           = TestMessage("mapdb", 2021)
    val msgSerialized = AkkaSerialization.serialize(serialization)(msg)

    val row = SnapshotRow(
      "pid",
      1,
      Instant.EPOCH.toEpochMilli,
      msgSerialized.success.value.payload,
      msgSerialized.success.value.serId,
      msgSerialized.success.value.serManifest
    )

    val result = AkkaSerialization.fromSnapshotRow(serialization)(row)
    val actual = result.success.value
    actual.metadata mustBe SnapshotMetadata("pid", 1, Instant.EPOCH.toEpochMilli)
    actual.snapshot mustBe a[TestMessage]

  }

  override protected def systemConfig: Config = {
    ConfigFactory.parseString("""
        |akka.actor {
        |  serialization-bindings {
        |    "testing.AkkaSerializable" = jackson-cbor
        |  }
        |}
        |""".stripMargin)

  }

}
