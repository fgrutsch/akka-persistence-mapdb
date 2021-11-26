package testing

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, TestSuite}

import java.util.UUID
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

trait TestActorSystem extends BeforeAndAfterAll with ScalaFutures { this: TestSuite =>

  implicit protected val actorSystem: ActorSystem = ActorSystem(UUID.randomUUID.toString, Some(systemConfig))
  implicit protected val ec: ExecutionContext     = actorSystem.dispatcher
  implicit protected val askTimeout: Timeout      = Timeout(10.seconds)

  protected def systemConfig: Config = ConfigFactory.load()

  abstract override protected def afterAll(): Unit = {
    super.afterAll()
    actorSystem.terminate().futureValue
  }

}
