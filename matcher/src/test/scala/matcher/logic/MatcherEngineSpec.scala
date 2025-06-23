package matcher.logic

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import model._

class MatcherEngineSpec extends AnyFunSuite with Matchers {

  test("matchAlert should detect exact match when keepOrder is true") {
    val term = QueryTerm(1, "IG Metall", "de", keepOrder = true)
    val alert = Alert(
      id = "a1",
      contents = List(AlertContent("We support IG Metall fully", "text", "de")),
      date = "2020-01-01T00:00:00Z",
      inputType = "tweet"
    )
    val result = MatcherEngine.matchAlert(alert, List(term))
    result should contain only MatchResult("a1", 1)
  }

  test("matchAlert should not match if keepOrder=true and order is wrong") {
    val term = QueryTerm(1, "IG Metall", "de", keepOrder = true)
    val alert = Alert(
      id = "a2",
      contents = List(AlertContent("Metall and IG are mentioned", "text", "de")),
      date = "2020-01-01T00:00:00Z",
      inputType = "tweet"
    )
    val result = MatcherEngine.matchAlert(alert, List(term))
    result shouldBe empty
  }

  test("matchAlert should match if keepOrder=false and words are shuffled") {
    val term = QueryTerm(1, "IG Metall", "de", keepOrder = false)
    val alert = Alert(
      id = "a3",
      contents = List(AlertContent("Metall and IG support workers", "text", "de")),
      date = "2020-01-01T00:00:00Z",
      inputType = "tweet"
    )
    val result = MatcherEngine.matchAlert(alert, List(term))
    result should contain only MatchResult("a3", 1)
  }

  test("matchAlert should not match if words are missing") {
    val term = QueryTerm(1, "IG Metall", "de", keepOrder = false)
    val alert = Alert(
      id = "a4",
      contents = List(AlertContent("Only IG is here", "text", "de")),
      date = "2020-01-01T00:00:00Z",
      inputType = "tweet"
    )
    val result = MatcherEngine.matchAlert(alert, List(term))
    result shouldBe empty
  }

  test("matchAlert should ignore alerts with wrong language") {
    val term = QueryTerm(1, "IG Metall", "de", keepOrder = true)
    val alert = Alert(
      id = "a5",
      contents = List(AlertContent("IG Metall appears here", "text", "en")),
      date = "2020-01-01T00:00:00Z",
      inputType = "tweet"
    )
    val result = MatcherEngine.matchAlert(alert, List(term))
    result shouldBe empty
  }
}
