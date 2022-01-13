/*
 * Copyright 2022 akka-persistence-mapdb contributors
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

package com.fgrutsch.akka.persistence.mapdb.query.javadsl

import akka.NotUsed
import akka.persistence.query.javadsl._
import akka.persistence.query.{EventEnvelope, Offset}
import akka.stream.javadsl.Source
import com.fgrutsch.akka.persistence.mapdb.query.scaladsl.{MapDbReadJournal => ScalaMapDbReadJournal}

object MapDbReadJournal {
  final val Identifier = ScalaMapDbReadJournal.Identifier
}

class MapDbReadJournal(scalaReadJournal: ScalaMapDbReadJournal)
    extends ReadJournal
    with CurrentPersistenceIdsQuery
    with PersistenceIdsQuery
    with CurrentEventsByPersistenceIdQuery
    with EventsByPersistenceIdQuery
    with CurrentEventsByTagQuery
    with EventsByTagQuery {

  override def currentPersistenceIds(): Source[String, NotUsed] = {
    scalaReadJournal.currentPersistenceIds().asJava
  }

  override def persistenceIds(): Source[String, NotUsed] = { scalaReadJournal.persistenceIds().asJava }

  override def currentEventsByPersistenceId(
      persistenceId: String,
      fromSequenceNr: Long,
      toSequenceNr: Long): Source[EventEnvelope, NotUsed] = {
    scalaReadJournal.currentEventsByPersistenceId(persistenceId, fromSequenceNr, toSequenceNr).asJava
  }

  override def eventsByPersistenceId(
      persistenceId: String,
      fromSequenceNr: Long,
      toSequenceNr: Long): Source[EventEnvelope, NotUsed] = {
    scalaReadJournal.eventsByPersistenceId(persistenceId, fromSequenceNr, toSequenceNr).asJava
  }

  override def currentEventsByTag(tag: String, offset: Offset): Source[EventEnvelope, NotUsed] = {
    scalaReadJournal.currentEventsByTag(tag, offset).asJava
  }

  override def eventsByTag(tag: String, offset: Offset): Source[EventEnvelope, NotUsed] = {
    scalaReadJournal.eventsByTag(tag, offset).asJava
  }
}
