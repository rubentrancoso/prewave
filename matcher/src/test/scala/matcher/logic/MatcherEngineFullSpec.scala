package matcher.logic

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import model._

/**
 * Unit tests for the MatcherEngine.matchAlert function.
 * Verifies various scenarios including order sensitivity, language filtering,
 * case insensitivity, and punctuation robustness.
 */
class MatcherEngineFullSpec extends AnyFunSuite with Matchers {

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

  test("matchAlert should only match content in correct language when multiple contents exist") {
    val term = QueryTerm(1, "IG Metall", "de", keepOrder = true)
    val alert = Alert(
      id = "a6",
      contents = List(
        AlertContent("Some text in English mentioning IG Metall", "text", "en"),
        AlertContent("Wir unterstützen IG Metall", "text", "de")
      ),
      date = "2020-01-01T00:00:00Z",
      inputType = "tweet"
    )
    val result = MatcherEngine.matchAlert(alert, List(term))
    result should contain only MatchResult("a6", 1)
  }

  test("matchAlert should match single-word term") {
    val term = QueryTerm(2, "Strike", "en", keepOrder = false)
    val alert = Alert(
      id = "a7",
      contents = List(AlertContent("The workers started a strike today", "text", "en")),
      date = "2020-01-01T00:00:00Z",
      inputType = "tweet"
    )
    val result = MatcherEngine.matchAlert(alert, List(term))
    result should contain only MatchResult("a7", 2)
  }

  test("matchAlert should handle case-insensitive matching") {
    val term = QueryTerm(3, "ig metall", "en", keepOrder = true)
    val alert = Alert(
      id = "a8",
      contents = List(AlertContent("We stand with IG METALL", "text", "en")),
      date = "2020-01-01T00:00:00Z",
      inputType = "tweet"
    )
    val result = MatcherEngine.matchAlert(alert, List(term))
    result should contain only MatchResult("a8", 3)
  }

  test("matchAlert should ignore punctuation") {
    val term = QueryTerm(4, "IG Metall", "de", keepOrder = true)
    val alert = Alert(
      id = "a9",
      contents = List(AlertContent("IG. Metall ist stark!", "text", "de")),
      date = "2020-01-01T00:00:00Z",
      inputType = "tweet"
    )
    val result = MatcherEngine.matchAlert(alert, List(term))
    result should contain only MatchResult("a9", 4)
  }

  test("matchAlert should not match if all contents are wrong language") {
    val term = QueryTerm(5, "IG Metall", "de", keepOrder = true)
    val alert = Alert(
      id = "a10",
      contents = List(
        AlertContent("Support IG Metall", "text", "en"),
        AlertContent("IG Metall erscheint hier", "text", "fr")
      ),
      date = "2020-01-01T00:00:00Z",
      inputType = "tweet"
    )
    val result = MatcherEngine.matchAlert(alert, List(term))
    result shouldBe empty
  }
}
