package matcher.client

import cats.effect._
import org.http4s.client.dsl.io._
import org.http4s.circe._
import org.http4s.ember.client._
import org.http4s._
import io.circe.generic.auto._
import model.Alert
import org.http4s.circe.CirceEntityDecoder._
import matcher.config.ConfigLoader

object AlertClient {
  private val uri: Uri = Uri.fromString(ConfigLoader.alertUrl) match {
    case Right(u) => u
    case Left(err) => throw new RuntimeException(s"Invalid alert URL: $err")
  }

  def fetchAlerts: IO[List[Alert]] =
    EmberClientBuilder.default[IO].build.use { client =>
      client.expect[List[Alert]](Request[IO](Method.GET, uri))
    }
}

