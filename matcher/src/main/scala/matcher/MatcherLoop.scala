package matcher

import cats.effect._
import scala.concurrent.duration._
import matcher.client._
import matcher.logic._
import matcher.memory._
import model._

/**
 * Main polling loop that continuously fetches alerts and matches them against the query terms.
 *
 * This class orchestrates the data flow:
 *   1. Retrieves alerts from the configured source.
 *   2. Filters out previously seen alerts using an in-memory cache.
 *   3. Performs term matching via `MatcherEngine`.
 *   4. Prints matching results to the console.
 *
 * @param alertClient The HTTP client used to retrieve alerts.
 */
class MatcherLoop(alertClient: AlertClient) {

  /**
   * Starts the loop that runs every 10 seconds to fetch and process alerts.
   *
   * @param terms The list of query terms used for matching.
   * @return An infinite IO effect that never completes under normal conditions.
   */
  def run(terms: List[QueryTerm]): IO[Unit] = {
    val processOnce = for {
      // Attempt to fetch alerts from the alert client; on failure, return an empty list.
      alerts <- alertClient.fetchAlerts.handleErrorWith { err =>
        IO.println(s"[Warning] Could not access the alert server (${err.getMessage}). Retrying in 10 seconds...") *>
          IO.pure(List.empty[Alert])
      }

      // Filter out already seen alert IDs
      newAlerts = alerts.filter(a => AlertCache.isNew(a.id))
      _ <- IO.println(s"Fetched ${newAlerts.size} new alerts...")

      // Perform matching logic
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
      _ <- IO.sleep(10.seconds) // Pause before the next cycle
    } yield ()

    // Run the matching process in an infinite, fault-tolerant loop
    processOnce.foreverM.handleErrorWith { err =>
      IO.println(s"[Unexpected Error] ${err.getMessage}") >> IO.sleep(10.seconds)
    }
  }
}
