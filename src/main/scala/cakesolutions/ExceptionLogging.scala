package cakesolutions

import java.io.{PrintWriter, StringWriter}

/**
 * Trait that adds in utility functions for aiding the logging of exception messages.
 */
trait ExceptionLogging {
  /**
   * Used to generate exception string messages for use (typically) during logging.
   *
   * @param cause the exception that has been thrown/generated
   * @return      the string representation of this exception
   */
  def exceptionString(cause: Throwable): String = {
    val sw = new StringWriter()
    val pw = new PrintWriter(sw, true)
    cause.printStackTrace(pw)
    sw.getBuffer.toString
  }
}
