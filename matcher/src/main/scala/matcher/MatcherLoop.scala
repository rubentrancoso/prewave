package matcher

import cats.effect._
import scala.concurrent.duration._
import matcher.client._
import matcher.logic._
import matcher.memory._
import model._

class MatcherLoop(alertClient: AlertClient) {
  def run(terms: List[QueryTerm]): IO[Unit] = {
    val processOnce = for {
      alerts <- alertClient.fetchAlerts.handleErrorWith { err =>
        IO.println(s"[Warning] Could not access the alert server (${err.getMessage}). Retrying in 10 seconds...") *>
          IO.pure(List.empty[Alert])
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
    } yield ()

    // Executa o loop infinitamente, de forma segura e idiomática
    processOnce.foreverM.handleErrorWith { err =>
      IO.println(s"[Unexpected Error] ${err.getMessage}") >> IO.sleep(10.seconds)
    }
  }
}
