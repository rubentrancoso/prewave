package matcher.config

import com.typesafe.config.ConfigFactory

object ConfigLoader {
  private val config = ConfigFactory.load()

  private val useRemote = config.getBoolean("prewave.useRemote")

  val alertUrl: String = if (useRemote)
    config.getString("prewave.endpoints.alerts")
  else
    config.getString("prewave.endpoints.localAlerts")

  val termUrl: String = if (useRemote)
    config.getString("prewave.endpoints.terms")
  else
    config.getString("prewave.endpoints.localTerms")
}
