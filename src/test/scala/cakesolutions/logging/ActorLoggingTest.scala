package cakesolutions.logging

import java.util.UUID

import akka.actor.ActorDSL._
import akka.actor.ActorSystem
import akka.event.Logging.MDC
import akka.testkit.EventFilter
import org.scalatest.FreeSpec

class ActorLoggingTest extends FreeSpec {

  implicit val system = ActorSystem()

  "Actor lifcycle MDC logging" in {
    val act = EventFilter.info(message = "$anon$1 started", occurrences = 1) intercept {
      actor(new Act with ActorLogging {
        override def mdc(msg: Any): MDC =
          Map(
            "traceId" -> UUID.randomUUID()
          )

        override def receive = {
          case msg @ "event" =>
            log.info(s"Received $msg")

          case msg @ "exception" =>
            log.error(s"Received $msg")
            throw new Exception("Fake exception")

          case msg @ "shutdown" =>
            log.debug(s"Received $msg")
            system.terminate()
        }
      })
    }

    EventFilter.info(message = "Received event", occurrences = 1) intercept {
      EventFilter.debug(start = "$anon$1 received String ", occurrences = 1) intercept {
        act ! "event"
      }
    }
    EventFilter.error(message = "Received exception", occurrences = 1) intercept {
      EventFilter.debug(start = "$anon$1 received String ", occurrences = 1) intercept {
        EventFilter.warning(start = "$anon$1 restarting whilst handling Some(String) ", occurrences = 1) intercept {
          EventFilter.info(message = "$anon$1 stopping", occurrences = 1) intercept {
            EventFilter.info(message = "$anon$1 started", occurrences = 1) intercept {
              act ! "exception"
            }
          }
        }
      }
    }
    EventFilter.debug(message = "Received shutdown", occurrences = 1) intercept {
      EventFilter.info(message = "$anon$1 stopping", occurrences = 1) intercept {
        EventFilter.debug(start = "$anon$1 received String ", occurrences = 1) intercept {
          act ! "shutdown"
        }
      }
    }
  }

}
