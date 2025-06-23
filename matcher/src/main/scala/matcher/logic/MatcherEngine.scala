package matcher.logic

import model._

object MatcherEngine {
  def matchAlert(alert: Alert, terms: List[QueryTerm]): List[MatchResult] = {
    terms.flatMap { term =>
      if (alert.contents.exists(c =>
        c.language == term.language && matches(term, c.text.toLowerCase)
      )) Some(MatchResult(alert.id, term.id)) else None
    }
  }

  private def matches(term: QueryTerm, text: String): Boolean = {
    val termParts = term.text.toLowerCase.split("\\s+")
    if (term.keepOrder) text.contains(term.text.toLowerCase)
    else termParts.forall(text.contains)
  }
}