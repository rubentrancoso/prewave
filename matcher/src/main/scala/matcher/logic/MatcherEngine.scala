package matcher.logic

import model.{Alert, QueryTerm, MatchResult}

/**
 * MatcherEngine is responsible for matching textual contents of alerts
 * against a list of query terms, respecting the language and word order rules.
 */
object MatcherEngine {

  /**
   * Matches an alert's content against all query terms.
   *
   * For each content block within the alert, only terms that match the same language
   * are considered. Depending on the `keepOrder` flag, matching logic will vary.
   *
   * @param alert The incoming alert to be analyzed
   * @param terms The list of predefined query terms
   * @return A list of MatchResult records indicating which terms matched which alerts
   */
  def matchAlert(alert: Alert, terms: List[QueryTerm]): List[MatchResult] = {
    val contents = alert.contents.filter(_.text.nonEmpty)

    contents.flatMap { content =>
      val text = content.text
      val lang = content.language

      terms
        .filter(t => t.language == lang && matches(text, t))
        .map(t => MatchResult(alert.id, t.id))
    }
  }

  /**
   * Determines whether a single term matches the given text.
   *
   * If keepOrder is true, the term must appear as an exact phrase.
   * Otherwise, all words in the term must appear in any order.
   *
   * @param text The full text to search within
   * @param term The term to match
   * @return True if the term matches the text; false otherwise
   */
  def matches(text: String, term: QueryTerm): Boolean = {
    val tokens = tokenize(text)
    val termTokens = tokenize(term.text)

    if (term.keepOrder)
      tokens.sliding(termTokens.length).contains(termTokens)
    else
      termTokens.forall(tokens.contains)
  }

  /**
   * Normalizes and tokenizes a given text into lowercase word tokens.
   * Non-letter/digit characters are removed before splitting.
   *
   * @param text The input string to tokenize
   * @return A list of clean lowercase tokens
   */
  private def tokenize(text: String): List[String] =
    text.toLowerCase
      .replaceAll("[^\\p{L}\\p{Nd}]+", " ")
      .split("\\s+")
      .filter(_.nonEmpty)
      .toList
}
