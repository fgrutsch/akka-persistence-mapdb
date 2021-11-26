package com.fgrutsch.akka.persistence.mapdb.query

import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import akka.persistence.query.{EventEnvelope, PersistenceQuery, Sequence}
import akka.stream.testkit.TestSubscriber
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestProbe
import com.fgrutsch.akka.persistence.mapdb.db.MapDbExtension
import com.fgrutsch.akka.persistence.mapdb.journal.{JournalConfig, MapDbJournalRepository}
import com.fgrutsch.akka.persistence.mapdb.query.scaladsl.MapDbReadJournal
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.funsuite.FixtureAnyFunSuite
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{Assertion, BeforeAndAfterEach, Outcome}
import testing.TestActorSystem

import scala.concurrent.Future
import scala.concurrent.duration.{DurationInt, FiniteDuration}

object MapDbReadJournalSpec {
  final val TestActorsRange = 1 to 3
}

trait MapDbReadJournalSpec
    extends FixtureAnyFunSuite
    with TestActorSystem
    with Matchers
    with ScalaFutures
    with Eventually
    with BeforeAndAfterEach {

  private val db            = MapDbExtension(actorSystem).database
  private val journalConfig = new JournalConfig(actorSystem.settings.config.getConfig("mapdb-journal"))
  private val journalRepo   = new MapDbJournalRepository(db, journalConfig.db)
  private val readJournal = PersistenceQuery(actorSystem).readJournalFor[MapDbReadJournal](MapDbReadJournal.Identifier)

  protected def verifyJournalCount(expected: Int): Assertion = {
    val verify = () => {
      val sumPerPid = MapDbReadJournalSpec.TestActorsRange
        .map(i => readJournal.currentEventsByPersistenceId(s"pid-$i", 0, Long.MaxValue).map(_ => 1).runFold(0)(_ + _))

      val result = Future
        .sequence(sumPerPid)
        .map(_.sum)

      whenReady(result) { total =>
        total mustBe expected
      }
    }

    eventually(verify())
  }

  protected def withCurrentPersistenceIds(within: FiniteDuration = 5.seconds)(
      f: TestSubscriber.Probe[String] => Unit): Unit = {
    val tp = readJournal.currentPersistenceIds().runWith(TestSink.probe[String])
    tp.within(within)(f(tp))
  }

  protected def withPersistenceIds(within: FiniteDuration = 5.seconds)(
      f: TestSubscriber.Probe[String] => Unit): Unit = {
    val tp = readJournal.persistenceIds().runWith(TestSink.probe[String])
    tp.within(within)(f(tp))
  }

  protected def withCurrentEventsByPersistenceId(
      within: FiniteDuration = 5.seconds)(pid: String, fromSeqNr: Long, toSeqNr: Long)(
      f: TestSubscriber.Probe[EventEnvelope] => Unit): Unit = {
    val tp = readJournal.currentEventsByPersistenceId(pid, fromSeqNr, toSeqNr).runWith(TestSink.probe[EventEnvelope])
    tp.within(within)(f(tp))
  }

  protected def withEventsByPersistenceId(
      within: FiniteDuration = 5.seconds)(pid: String, fromSeqNr: Long, toSeqNr: Long)(
      f: TestSubscriber.Probe[EventEnvelope] => Unit): Unit = {
    val tp = readJournal.eventsByPersistenceId(pid, fromSeqNr, toSeqNr).runWith(TestSink.probe[EventEnvelope])
    tp.within(within)(f(tp))
  }

  protected def withCurrentEventsByTag(within: FiniteDuration = 5.seconds)(tag: String, offset: Long)(
      f: TestSubscriber.Probe[EventEnvelope] => Unit): Unit = {
    val tp = readJournal.currentEventsByTag(tag, Sequence(offset)).runWith(TestSink.probe[EventEnvelope])
    tp.within(within)(f(tp))
  }

  protected def withEventsByTag(within: FiniteDuration = 5.seconds)(tag: String, offset: Long)(
      f: TestSubscriber.Probe[EventEnvelope] => Unit): Unit = {
    val tp = readJournal.eventsByTag(tag, Sequence(offset)).runWith(TestSink.probe[EventEnvelope])
    tp.within(within)(f(tp))
  }

  override protected def systemConfig: Config = {
    val serializationConfig = ConfigFactory.parseString("""
      |akka.actor {
      |  serialization-bindings {
      |    "testing.AkkaSerializable" = jackson-cbor
      |  }
      |}
      |""".stripMargin)
    ConfigFactory.load("memory-db").withFallback(serializationConfig)
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    journalRepo.clear().futureValue
  }

  implicit override def patienceConfig: PatienceConfig = PatienceConfig(Span(10, Seconds))

  override protected def withFixture(test: OneArgTest): Outcome = {
    val actorRefs = MapDbReadJournalSpec.TestActorsRange
      .map(i => actorSystem.actorOf(Props(new TestPersistenceActor(i))))
      .toList

    try {
      Future.sequence(actorRefs.map(_ ? TestPersistenceActor.Command.GetState)).futureValue
      val (a1, a2, a3) = actorRefs.toArray match {
        case Array(a1, a2, a3) => (a1, a2, a3)
        case _ => fail(s"Expected exactly ${MapDbReadJournalSpec.TestActorsRange.size} TestPersistentActors")
      }
      super.withFixture(test.toNoArgTest(FixtureParam(a1, a2, a3)))
    } finally {
      val tp = TestProbe()
      actorRefs.foreach { a =>
        tp.watch(a)
        actorSystem.stop(a)
        tp.expectTerminated(a)
      }
    }
  }

  case class FixtureParam(actor1: ActorRef, actor2: ActorRef, actor3: ActorRef)

  implicit class ProbeOps(probe: TestSubscriber.Probe[EventEnvelope]) {
    def expectNextEventEnvelope(
        persistenceId: String,
        seqNr: Long,
        event: TestPersistenceActor.Event): TestSubscriber.Probe[EventEnvelope] = {
      val envelope = probe.expectNext()

      envelope.persistenceId mustBe persistenceId
      envelope.sequenceNr mustBe seqNr
      envelope.event mustBe event
      probe
    }

    def expectNextEventEnvelope(
        persistenceId: String,
        offset: Sequence,
        event: TestPersistenceActor.Event): TestSubscriber.Probe[EventEnvelope] = {
      val envelope = probe.expectNext()

      envelope.persistenceId mustBe persistenceId
      envelope.offset mustBe offset
      envelope.event mustBe event
      probe
    }
  }

}
