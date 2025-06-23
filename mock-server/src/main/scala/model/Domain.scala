package model

case class QueryTerm(
                      id: Int,
                      text: String,
                      language: String,
                      keepOrder: Boolean
                    )

case class AlertContent(
                         text: String,
                         `type`: String,
                         language: String
                       )

case class Alert(
                  id: String,
                  contents: List[AlertContent],
                  date: String,
                  inputType: String
                )

case class MatchResult(
                        alertId: String,
                        queryTermId: Int
                      )

