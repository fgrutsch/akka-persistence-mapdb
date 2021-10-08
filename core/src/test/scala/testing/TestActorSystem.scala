package testing

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import org.scalatest.{BeforeAndAfterAll, TestSuite}

import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration._

trait TestActorSystem extends BeforeAndAfterAll { this: TestSuite =>

  protected val actorSystem = ActorSystem[Unit](Behaviors.empty, UUID.randomUUID.toString)

  abstract override protected def afterAll(): Unit = {
    try {
      actorSystem.terminate()
      Await.result(actorSystem.whenTerminated, 5.seconds)
    } finally {
      super.afterAll()
    }
  }

}
