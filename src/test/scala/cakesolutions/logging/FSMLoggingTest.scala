package cakesolutions.logging

import java.util.UUID

import akka.actor.ActorDSL._
import akka.actor.ActorSystem
import akka.event.Logging._
import akka.testkit.EventFilter
import org.scalatest.FreeSpec

class FSMLoggingTest extends FreeSpec {

  implicit val system = ActorSystem()

  "Actor FSM MDC logging" in {
    val act = EventFilter.info(message = "$anon$1 started", occurrences = 1) intercept {
      actor(new Act with FSMLogging[String, Int] {
        override def mdc(msg: Any): MDC =
          Map(
            "traceId" -> UUID.randomUUID()
          )

        startWith("init", 0)

        when("init") {
          case msg @ Event("event", st) =>
            log.info(s"Received $msg")
            goto("next") using 1
        }

        when("next") {
          case msg @ Event("next", st) =>
            log.error(s"Received $msg")
            goto("final") using 2
        }

        when("final") {
          case msg @ Event("shutdown", st) =>
            log.debug(s"Received $msg")
            stop()
        }

        onTermination {
          case StopEvent(_, "final", _) =>
            system.terminate()
        }

        initialize()
      })
    }

    EventFilter.info(message = "Received Event(event,0)", occurrences = 1) intercept {
      EventFilter.debug(start = "$anon$1 received String from ", occurrences = 1) intercept {
        EventFilter.debug(start = "processing Event(event,0) from ", occurrences = 1) intercept {
          EventFilter.debug(message = "transition init -> next", occurrences = 1) intercept {
            act ! "event"
          }
        }
      }
    }
    EventFilter.error(message = "Received Event(next,1)", occurrences = 1) intercept {
      EventFilter.debug(start = "$anon$1 received String from ", occurrences = 1) intercept {
        EventFilter.debug(start = "processing Event(next,1) from ", occurrences = 1) intercept {
          EventFilter.debug(message = "transition next -> final", occurrences = 1) intercept {
            act ! "next"
          }
        }
      }
    }
    EventFilter.debug(message = "Received Event(shutdown,2)", occurrences = 1) intercept {
      EventFilter.debug(start = "$anon$1 received String from ", occurrences = 1) intercept {
        EventFilter.debug(start = "processing Event(shutdown,2) from ", occurrences = 1) intercept {
          EventFilter.debug(start = "Stopping $anon$1: 20 events from logging trace buffers:", occurrences = 1) intercept {
            EventFilter.info(message = "$anon$1 stopping", occurrences = 1) intercept {
              act ! "shutdown"
            }
          }
        }
      }
    }
  }

}
