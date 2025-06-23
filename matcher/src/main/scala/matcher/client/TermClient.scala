package matcher.client

import cats.effect._
import org.http4s.client.dsl.io._
import org.http4s.circe._
import org.http4s.ember.client._
import org.http4s._
import io.circe.generic.auto._
import model.QueryTerm
import org.http4s.circe.CirceEntityDecoder._ // <- Importa implicits para .expect[List[T]]
import matcher.config.ConfigLoader

object TermClient {
  private val uri: Uri = Uri.fromString(ConfigLoader.termUrl) match {
    case Right(u) => u
    case Left(err) => throw new RuntimeException(s"Invalid alert URL: $err")
  }

  def fetchTerms: IO[List[QueryTerm]] =
    EmberClientBuilder.default[IO].build.use { client =>
      client.expect[List[QueryTerm]](Request[IO](Method.GET, uri))
    }
}