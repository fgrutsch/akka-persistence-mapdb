/*
 * Copyright 2021 akka-persistence-mapdb contributors
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

package com.fgrutsch.akka.persistence.mapdb.snapshot

import akka.actor.ActorSystem
import akka.persistence.snapshot.SnapshotStore
import akka.persistence.{SelectedSnapshot, SnapshotMetadata, SnapshotSelectionCriteria}
import akka.serialization.SerializationExtension
import com.fgrutsch.akka.persistence.mapdb.db.MapDbExtension
import com.fgrutsch.akka.persistence.mapdb.util.AkkaSerialization
import com.typesafe.config.Config

import scala.concurrent.{ExecutionContext, Future}

class MapDbSnapshotStore(config: Config) extends SnapshotStore {

  implicit val system: ActorSystem          = context.system
  implicit private val ec: ExecutionContext = system.dispatcher
  private val serialization                 = SerializationExtension(system)
  private val snapshotConfig                = new SnapshotConfig(config)

  private val db   = MapDbExtension(system).database
  private val repo = new MapDbSnapshotRepository(db, snapshotConfig.db)

  override def loadAsync(
      persistenceId: String,
      criteria: SnapshotSelectionCriteria): Future[Option[SelectedSnapshot]] = {
    val snapshotRow = criteria match {
      case SnapshotSelectionCriteria(Long.MaxValue, Long.MaxValue, _, _) =>
        val filter = MapDbSnapshotRepository.FindFilter()
        repo.find(persistenceId, filter)
      case SnapshotSelectionCriteria(Long.MaxValue, maxTimestamp, _, _) =>
        val filter = MapDbSnapshotRepository.FindFilter(None, Some(maxTimestamp))
        repo.find(persistenceId, filter)
      case SnapshotSelectionCriteria(maxSequenceNr, Long.MaxValue, _, _) =>
        val filter = MapDbSnapshotRepository.FindFilter(Some(maxSequenceNr), None)
        repo.find(persistenceId, filter)
      case SnapshotSelectionCriteria(maxSequenceNr, maxTimestamp, _, _) =>
        val filter = MapDbSnapshotRepository.FindFilter(Some(maxSequenceNr), Some(maxTimestamp))
        repo.find(persistenceId, filter)
    }

    snapshotRow
      .flatMap {
        case Some(row) =>
          val selectedSnapshot = AkkaSerialization.fromSnapshotRow(serialization)(row)
          Future.fromTry(selectedSnapshot).map(Option(_))
        case None =>
          Future.successful(None)
      }
  }

  override def saveAsync(metadata: SnapshotMetadata, snapshot: Any): Future[Unit] = {
    val eventualSnapshot = Future.fromTry(serialize(metadata, snapshot))
    eventualSnapshot.flatMap(repo.save)
  }

  override def deleteAsync(metadata: SnapshotMetadata): Future[Unit] = {
    val filter = MapDbSnapshotRepository.DeleteFilter(sequenceNr = Some(metadata.sequenceNr))
    repo.delete(metadata.persistenceId, filter)
  }

  override def deleteAsync(persistenceId: String, criteria: SnapshotSelectionCriteria): Future[Unit] = {
    criteria match {
      case SnapshotSelectionCriteria(Long.MaxValue, Long.MaxValue, _, _) =>
        val filter = MapDbSnapshotRepository.DeleteFilter()
        repo.delete(persistenceId, filter)
      case SnapshotSelectionCriteria(Long.MaxValue, maxTimestamp, _, _) =>
        val filter = MapDbSnapshotRepository.DeleteFilter(maxTimestamp = Some(maxTimestamp))
        repo.delete(persistenceId, filter)
      case SnapshotSelectionCriteria(maxSequenceNr, Long.MaxValue, _, _) =>
        val filter = MapDbSnapshotRepository.DeleteFilter(maxSequenceNr = Some(maxSequenceNr))
        repo.delete(persistenceId, filter)
      case SnapshotSelectionCriteria(maxSequenceNr, maxTimestamp, _, _) =>
        val filter = MapDbSnapshotRepository.DeleteFilter(Some(maxSequenceNr), Some(maxTimestamp))
        repo.delete(persistenceId, filter)
    }
  }

  private def serialize(meta: SnapshotMetadata, snapshot: Any) = {
    AkkaSerialization
      .serialize(serialization)(snapshot)
      .map { serialized =>
        SnapshotRow(
          meta.persistenceId,
          meta.sequenceNr,
          meta.timestamp,
          serialized.payload,
          serialized.serId,
          serialized.serManifest
        )
      }

  }

}
