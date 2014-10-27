package cakesolutions

import com.typesafe.config.ConfigFactory

/**
 * Trait setting up application configuration data
 */
trait Configuration {

  val config = ConfigFactory.load()

}
