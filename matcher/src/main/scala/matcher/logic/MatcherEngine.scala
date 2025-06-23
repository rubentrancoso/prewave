package matcher.logic

import model.{Alert, QueryTerm}
import model.MatchResult

object MatcherEngine {

  def matchAlert(alert: Alert, terms: List[QueryTerm]): List[MatchResult] = {
    val text = alert.contents.find(_.language == alert.contents.head.language).map(_.text).getOrElse("")
    terms.filter(t => matches(text, t))
      .map(t => MatchResult(alert.id, t.id))
  }

  def matches(text: String, term: QueryTerm): Boolean = {
    val tokens = tokenize(text)
    val termTokens = tokenize(term.text)

    if (term.keepOrder)
      tokens.sliding(termTokens.length).contains(termTokens)
    else
      termTokens.forall(tokens.contains)
  }

  private def tokenize(text: String): List[String] =
    text.toLowerCase
      .replaceAll("[^\\p{L}\\p{Nd}]+", " ") // remove pontuação
      .split("\\s+")
      .filter(_.nonEmpty)
      .toList
}
