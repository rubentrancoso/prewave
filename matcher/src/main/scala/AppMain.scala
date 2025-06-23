import cats.effect.*
import matcher.MatcherLoop
import matcher.client.*

object AppMain extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    for {
      terms <- TermClient.fetchTerms
      _ <- IO.println(s"Loaded ${terms.size} terms.")
      _ <- MatcherLoop.run(terms)
    } yield ExitCode.Success
  }
}