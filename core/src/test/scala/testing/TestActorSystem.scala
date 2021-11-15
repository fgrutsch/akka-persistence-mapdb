package testing

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.{BeforeAndAfterAll, TestSuite}

import java.util.UUID
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

trait TestActorSystem extends BeforeAndAfterAll { this: TestSuite =>

  implicit protected val actorSystem: ActorSystem = ActorSystem(UUID.randomUUID.toString, Some(systemConfig))
  implicit protected val ec: ExecutionContext     = actorSystem.dispatcher

  protected def systemConfig: Config = ConfigFactory.load()

  abstract override protected def afterAll(): Unit = {
    try {
      actorSystem.terminate()
      Await.result(actorSystem.whenTerminated, 5.seconds)
    } finally {
      super.afterAll()
    }
  }

}
