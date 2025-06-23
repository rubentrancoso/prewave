package matcher

import cats.effect._
import scala.concurrent.duration._
import matcher.client._
import matcher.logic._
import matcher.memory._
import model._

object MatcherLoop {
  def run(terms: List[QueryTerm]): IO[Unit] = {
    val loop = for {
      alerts <- AlertClient.fetchAlerts.handleErrorWith { err =>
        IO.println(s"[Warning] Could not access the alert server (${err.getMessage}). Retrying in 10 seconds...") >>
          IO.sleep(10.seconds) >>
          IO.pure(List.empty[Alert]) // Retorna lista vazia para não interromper o loop
      }

      newAlerts = alerts.filter(a => AlertCache.isNew(a.id))
      _ <- IO.println(s"Fetched ${newAlerts.size} new alerts...")

      matches = newAlerts.flatMap(a => MatcherEngine.matchAlert(a, terms))
      _ <- IO {
        matches.foreach { m =>
          val alert = newAlerts.find(_.id == m.alertId).get
          val term = terms.find(_.id == m.queryTermId).get
          println(s"MatchResult(${m.alertId}, ${m.queryTermId})")
          println(s"\tAlert Text: '${alert.contents.head.text}'")
          println(s"\tMatched Term: '${term.text}' [keepOrder=${term.keepOrder}]\n")
        }
      }
      _ <- IO.println("Matching done.")
      _ <- IO.sleep(10.seconds)
      _ <- run(terms)
    } yield ()

    loop.handleErrorWith { err =>
      IO.println(s"[Erro inesperado] ${err.getMessage}") >> IO.sleep(10.seconds) >> run(terms)
    }
  }
}
