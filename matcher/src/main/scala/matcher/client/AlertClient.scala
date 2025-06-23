package matcher.client

import cats.effect._
import org.http4s._
import org.http4s.client._
import org.http4s.Method._
import io.circe.generic.auto._
import model.Alert
import matcher.config.ConfigLoader
import org.http4s.circe._                      // <-- ESTA LINHA resolve o problema
import org.http4s.circe.CirceEntityDecoder._   // <-- Também é suficiente se quiser ser mais explícito

class AlertClient(client: Client[IO]) {
  private val uri: Uri = Uri.fromString(ConfigLoader.alertUrl) match {
    case Right(u) => u
    case Left(err) => throw new RuntimeException(s"Invalid alert URL: $err")
  }

  def fetchAlerts: IO[List[Alert]] =
    client.expect[List[Alert]](Request[IO](GET, uri))
}
