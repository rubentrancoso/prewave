package model

/**
 * Represents a search term used to match against alerts.
 *
 * @param id         Unique identifier of the query term.
 * @param text       The text content of the query term.
 * @param language   The language code (e.g., "en", "de") for this term.
 * @param keepOrder  Whether the term must appear in the same order in the alert text.
 */
case class QueryTerm(
                      id: Int,
                      text: String,
                      language: String,
                      keepOrder: Boolean
                    )

/**
 * Represents a single content block within an alert.
 *
 * @param text      The alert message body.
 * @param `type`    Type of content (e.g., "text", "image", etc.).
 * @param language  Language code associated with this content.
 */
case class AlertContent(
                         text: String,
                         `type`: String,
                         language: String
                       )

/**
 * Represents an incoming alert that may contain one or more content blocks.
 *
 * @param id         Unique identifier of the alert.
 * @param contents   List of content elements (text, type, language).
 * @param date       Timestamp string in ISO format.
 * @param inputType  Source type (e.g., "tweet", "news", etc.).
 */
case class Alert(
                  id: String,
                  contents: List[AlertContent],
                  date: String,
                  inputType: String
                )

/**
 * Represents a successful match between a query term and an alert.
 *
 * @param alertId      ID of the alert where the match occurred.
 * @param queryTermId  ID of the query term that matched the alert.
 */
case class MatchResult(
                        alertId: String,
                        queryTermId: Int
                      )
