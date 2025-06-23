package matcher.memory

import scala.collection.mutable

object AlertCache {
  private val seenIds: mutable.Set[String] = mutable.Set.empty

  def isNew(id: String): Boolean = synchronized {
    if (seenIds.contains(id)) false
    else {
      seenIds += id
      true
    }
  }
}