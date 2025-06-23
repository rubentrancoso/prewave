package server.service

import model.QueryTerm

object MockTermGenerator {
  def generateTerms(n: Int): List[QueryTerm] = {
    val texts = List(
      "IG Metall",
      "Solidarity",
      "Strike",
      "Climate Change",
      "Black Lives Matter",
      "Workers Union",
      "Minimum Wage",
      "Fair Trade",
      "Labor Rights",
      "Gender Equality",
      "Fridays for Future",
      "Extinction Rebellion",
      "Factory Protest",
      "Youth Movement",
      "Wage Theft",
      "Worker Safety",
      "Green Energy",
      "Unfair Dismissal",
      "Pension Reform",
      "Zero Hour Contracts",
      "Occupy Movement",
      "Automation Risk",
      "AI Displacement",
      "Human Rights",
      "Supply Chain Disruption",
      "Coal Phase Out",
      "Public Sector Strike",
      "Teachers Union",
      "Nurses Protest",
      "Rent Control",
      "Food Insecurity",
      "Gig Economy",
      "Union Busting",
      "Right to Organize",
      "Freedom of Assembly",
      "Migrant Labor",
      "Trade Embargo",
      "Export Ban",
      "Corporate Responsibility",
      "Whistleblower Report",
      "Social Dialogue",
      "Living Wage",
      "Employee Burnout",
      "Digital Surveillance",
      "Job Outsourcing",
      "Wage Gap",
      "Remote Work Policy",
      "Equal Pay",
      "Collective Bargaining",
      "Supply Chain Transparency"
    )

    val langs = List("en", "de")

    (1 to n).toList.map { i =>
      QueryTerm(
        id = 100 + i,
        text = texts((i - 1) % texts.length),
        language = langs(i % langs.length),
        keepOrder = i % 2 == 0
      )
    }
  }
}
