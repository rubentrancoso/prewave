package matcher.client

import cats.effect.*
import org.http4s.*
import org.http4s.client.*
import org.http4s.Method.*
import io.circe.generic.auto.*
import model.{Alert, QueryTerm}
import matcher.config.ConfigLoader
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.* // Enables automatic decoding of JSON into Scala types

/**
 * HTTP client responsible for fetching query terms from the configured API endpoint.
 *
 * @param client Reusable HTTP client instance (e.g., EmberClient) passed as dependency.
 */
class TermClient(client: Client[IO]) {

  // Load the appropriate URI from the application configuration
  private val uri: Uri = Uri.fromString(ConfigLoader.termUrl) match {
    case Right(u) => u
    case Left(err) => throw new RuntimeException(s"Invalid term URL: $err")
  }

  /**
   * Fetches a list of query terms from the configured Prewave endpoint.
   * Automatically decodes JSON responses into a List[QueryTerm].
   *
   * @return An effectful computation yielding a list of query terms.
   */
  def fetchTerms: IO[List[QueryTerm]] =
    client.expect[List[QueryTerm]](Request[IO](Method.GET, uri))
}
