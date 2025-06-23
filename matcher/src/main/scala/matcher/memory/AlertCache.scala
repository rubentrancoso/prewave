package matcher.memory

import scala.collection.mutable

/**
 * In-memory cache to track which alert IDs have already been processed.
 *
 * This prevents reprocessing of the same alert multiple times across polling cycles.
 *
 * Note: This cache is local to the process and will reset if the application restarts.
 */
object AlertCache {
  // A mutable set to store IDs of alerts that have been seen
  private val seenIds: mutable.Set[String] = mutable.Set.empty

  /**
   * Checks if a given alert ID has already been seen.
   *
   * If the ID is new, it is added to the cache and returns true.
   * If it has already been seen, returns false.
   *
   * This method is synchronized to ensure thread safety.
   *
   * @param id the ID of the alert
   * @return true if the alert is new, false if it has already been processed
   */
  def isNew(id: String): Boolean = synchronized {
    if (seenIds.contains(id)) false
    else {
      seenIds += id
      true
    }
  }
}
