package cakesolutions

package logging

import akka.actor.Actor
import akka.event.Logging.MDC
import akka.event.{Logging, DiagnosticLoggingAdapter}

/**
 * Trait to add specialised logging to Actors.
 *
 * Users just need to define an `mdc` method in order to take advantage of MDC logging.
 */
trait ActorLogging extends Actor {

  private val logFullMessages = context.system.settings.config.getBoolean("akka.log-full-messages")

  private def messageString(msg: Any): String = {
    if (logFullMessages) {
      // Privacy is not an issue, so log the actual message
      msg.toString
    } else {
      // Privacy is an issue, so log message type and rely on MDC for context
      msg.getClass.getSimpleName
    }
  }

  lazy val log: DiagnosticLoggingAdapter = Logging(this)

  /**
   * Extract MDC information from a given message.
   *
   * @param msg message we are to extract contextual information from
   */
  def mdc(msg: Any): MDC = Map.empty

  /**
   * We log when the actor has started.
   */
  override def preStart() = {
    log.info(s"${getClass.getSimpleName} started")
    super.preStart()
  }

  /**
   * We log when the actor restarts.
   *
   * @param cause the exception that has caused this restart
   * @param msg   (optional) the message that this actor was processing when it restarted
   */
  override def preRestart(cause: Throwable, msg: Option[Any]) = {
    log.warning(s"${getClass.getSimpleName} restarting whilst handling ${msg.map(messageString)} - reason: ${Logging.stackTraceFor(cause)}")
    super.preRestart(cause, msg)
  }

  /**
   * We log when the actor stops.
   */
  override def postStop() = {
    log.info(s"${getClass.getSimpleName} stopping")
    super.postStop()
  }
  
  /**
   * We intercept and DEBUG log all received messages.
   */
  override def aroundReceive(receive: Actor.Receive, msg: Any) = {
    log.mdc(mdc(msg))
    log.debug(s"${getClass.getSimpleName} received ${messageString(msg)} from ${sender()}")
    super.aroundReceive(receive, msg)
    log.clearMDC()
  }

  /**
   * We log unhandled messages.
   */
  override def unhandled(msg: Any) = {
    log.warning(s"${getClass.getSimpleName} received the unhandled message ${messageString(msg)}")
    super.unhandled(msg)
  }

}
