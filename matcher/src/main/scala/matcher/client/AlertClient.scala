package matcher.client

import cats.effect._
import org.http4s._
import org.http4s.client._
import org.http4s.Method._
import io.circe.generic.auto._
import model.Alert
import matcher.config.ConfigLoader
import org.http4s.circe._                      // Provides automatic JSON decoding
import org.http4s.circe.CirceEntityDecoder._   // Optional: explicitly brings implicit decoders into scope

/**
 * HTTP client responsible for fetching alert data from the configured API endpoint.
 *
 * @param client The pre-initialized HTTP4s client, injected for reusability and testability.
 */
class AlertClient(client: Client[IO]) {

  // Load the appropriate URI depending on config (remote or local mock server)
  private val uri: Uri = Uri.fromString(ConfigLoader.alertUrl) match {
    case Right(u) => u
    case Left(err) => throw new RuntimeException(s"Invalid alert URL: $err")
  }

  /**
   * Fetches a list of alerts from the configured Prewave endpoint.
   * JSON is automatically deserialized into a List[Alert] via Circe.
   *
   * @return An IO-wrapped list of alerts.
   */
  def fetchAlerts: IO[List[Alert]] =
    client.expect[List[Alert]](Request[IO](GET, uri))
}
