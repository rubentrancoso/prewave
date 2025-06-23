import cats.effect.*
import matcher.MatcherLoop
import matcher.client.{AlertClient, TermClient}
import matcher.logic.MatcherEngine
import matcher.memory.AlertCache
import model.QueryTerm
import org.http4s.ember.client.EmberClientBuilder

object AppMain extends IOApp.Simple {
  def run: IO[Unit] =
    EmberClientBuilder.default[IO].build.use { httpClient =>
      val alertClient = new AlertClient(httpClient)
      val termClient = new TermClient(httpClient)

      for {
        terms <- termClient.fetchTerms
        _ <- IO.println(s"Loaded ${terms.size} terms.")
        loop = new MatcherLoop(alertClient)
        _ <- loop.run(terms)
      } yield ()
    }
}
