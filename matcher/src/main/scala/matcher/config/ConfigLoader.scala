package matcher.config

import com.typesafe.config.ConfigFactory

/**
 * Singleton object responsible for loading and exposing configuration values
 * from the `application.conf` file using the Typesafe Config library.
 *
 * This loader supports switching between remote and local API endpoints
 * based on a boolean flag `prewave.useRemote`.
 */
object ConfigLoader {
  // Load configuration from application.conf (or fallback chain)
  private val config = ConfigFactory.load()

  // Determines whether to use remote endpoints or local mock endpoints
  private val useRemote = config.getBoolean("prewave.useRemote")

  /**
   * The URL to be used for fetching alerts.
   * It will point to either the remote or local endpoint depending on the `useRemote` flag.
   */
  val alertUrl: String = if (useRemote)
    config.getString("prewave.endpoints.alerts")
  else
    config.getString("prewave.endpoints.localAlerts")

  /**
   * The URL to be used for fetching query terms.
   * It will point to either the remote or local endpoint depending on the `useRemote` flag.
   */
  val termUrl: String = if (useRemote)
    config.getString("prewave.endpoints.terms")
  else
    config.getString("prewave.endpoints.localTerms")
}
