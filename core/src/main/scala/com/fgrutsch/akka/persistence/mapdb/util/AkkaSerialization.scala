/*
 * Copyright 2023 akka-persistence-mapdb contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fgrutsch.akka.persistence.mapdb.util

import akka.persistence.{PersistentRepr, SelectedSnapshot, SnapshotMetadata}
import akka.serialization.{Serialization, Serializers}
import com.fgrutsch.akka.persistence.mapdb.journal.JournalRow
import com.fgrutsch.akka.persistence.mapdb.snapshot.SnapshotRow

import scala.util.Try

private[mapdb] object AkkaSerialization {

  case class AkkaSerialized(serId: Int, serManifest: String, payload: Array[Byte])

  def serialize(serialization: Serialization)(payload: Any): Try[AkkaSerialized] = {
    val p2          = payload.asInstanceOf[AnyRef]
    val serializer  = serialization.findSerializerFor(p2)
    val serManifest = Serializers.manifestFor(serializer, p2)
    val serialized  = serialization.serialize(p2)
    serialized.map(payload => AkkaSerialized(serializer.identifier, serManifest, payload))
  }

  def fromJournalRow(serialization: Serialization)(row: JournalRow): Try[(PersistentRepr, Long)] = {
    serialization.deserialize(row.eventPayload, row.eventSerId, row.eventSerManifest).map { payload =>
      val persistentRepr = PersistentRepr(payload, row.sequenceNr, row.persistenceId, writerUuid = row.writer)
      (persistentRepr, row.ordering)
    }
  }

  def fromSnapshotRow(serialization: Serialization)(row: SnapshotRow): Try[SelectedSnapshot] = {
    serialization.deserialize(row.snapshotPayload, row.snapshotSerId, row.snapshotSerManifest).map { payload =>
      SelectedSnapshot(
        SnapshotMetadata(row.persistenceId, row.sequenceNr, row.created),
        payload
      )
    }
  }

}
