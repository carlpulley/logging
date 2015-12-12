package cakesolutions

package logging

import akka.actor.LoggingFSM
import akka.event.{Logging, DiagnosticLoggingAdapter}

/**
 * Trait to add specialised logging into FSM based actors.
 *
 * Users just need to define an `mdc` method in order to take advantage of MDC logging.
 */
trait FSMLogging[State, Data] extends LoggingFSM[State, Data] with ActorLogging {

  private val logFullMessages = context.system.settings.config.getBoolean("akka.log-full-messages")

  private def logTrace: String = {
    if (logFullMessages) {
      // Privacy is not an issue, so log the full message trace
      getLog.mkString("\n  ", ",\n  ", "")
    } else {
      // Privacy is an issue, so log message type and rely on MDC for context
      // NOTE: we assume that FSM `State` and `Date` are not sensitive
      getLog.map(entry => entry.copy(event = entry.event.getClass.getSimpleName)).mkString("\n  ", ",\n  ", "")
    }
  }

  override lazy val log: DiagnosticLoggingAdapter = Logging(this)

  // By default (i.e. in production etc.) this value should be zero - i.e. debug should not be enabled!
  override def logDepth: Int = {
    if (log.isDebugEnabled) {
      context.system.settings.config.getInt("akka.actor.debug.logging.depth")
    } else {
      0
    }
  }

  /**
   * For FSM actors, we also dump our trace buffers when we are restarted.
   *
   * @param cause the exception that has caused this restart
   * @param msg   (optional) the message that this actor was processing when it restarted
   */
  override def preRestart(cause: Throwable, msg: Option[Any]) = {
    log.warning(s"Restarting ${getClass.getSimpleName}: $logDepth events from logging trace buffers: $logTrace")
    super.preRestart(cause, msg)
  }

  /**
   * For FSM actors, we also dump our trace buffers when we are stopped.
   */
  override def postStop() = {
    log.debug(s"Stopping ${getClass.getSimpleName}: $logDepth events from logging trace buffers: $logTrace")
    super.postStop()
  }

}
