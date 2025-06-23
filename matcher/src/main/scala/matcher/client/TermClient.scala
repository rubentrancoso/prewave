package matcher.client

import cats.effect.*
import org.http4s.*
import org.http4s.client.*
import org.http4s.Method.*
import io.circe.generic.auto.*
import model.{Alert, QueryTerm}
import matcher.config.ConfigLoader
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.*   // <-- Também é suficiente se quiser ser mais explícito

class TermClient(client: Client[IO]) {
  private val uri: Uri = Uri.fromString(ConfigLoader.termUrl) match {
    case Right(u) => u
    case Left(err) => throw new RuntimeException(s"Invalid term URL: $err")
  }

  def fetchTerms: IO[List[QueryTerm]] =
    client.expect[List[QueryTerm]](Request[IO](Method.GET, uri))

}
