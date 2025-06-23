package server.service

import model.{Alert, AlertContent, QueryTerm}
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.util.Random

object MockAlertGenerator {
  private val extraNoiseWords = List("global", "movement", "now", "is", "rising", "fast", "demanding", "against", "with", "system", "call", "support", "city")

  // Simula o mesmo universo de termos gerados
  private val queryTerms = MockTermGenerator.generateTerms(50)

  def generateAlerts(count: Int): List[Alert] = {
    (1 to count).map { i =>
      val id = UUID.randomUUID().toString
      val term = queryTerms(Random.nextInt(queryTerms.length))

      val alertText = generateTextFromTerm(term)
      val language = term.language

      Alert(
        id = id,
        contents = List(AlertContent(alertText, "text", language)),
        date = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT),
        inputType = "tweet"
      )
    }.toList
  }

  private def generateTextFromTerm(term: QueryTerm): String = {
    val words = term.text.split("\\s+").toList
    val variation = Random.nextInt(4)

    def addNoiseAround(core: String): String = {
      val prefix = List.fill(Random.between(3, 6))(randomNoiseWord())
      val suffix = List.fill(Random.between(3, 6))(randomNoiseWord())
      (prefix :+ core :++ suffix).mkString(" ")
    }

    variation match {
      case 0 => // Exato (para keepOrder = true)
        addNoiseAround(term.text)

      case 1 => // Ordem embaralhada (para keepOrder = false)
        if (!term.keepOrder) addNoiseAround(Random.shuffle(words).mkString(" "))
        else addNoiseAround(term.text)

      case 2 => // Intercalado (para keepOrder = false)
        if (!term.keepOrder) {
          val interleaved = words.flatMap(w => List(w, randomNoiseWord()))
          addNoiseAround(interleaved.mkString(" "))
        } else addNoiseAround(term.text)

      case _ => // Alerta irrelevante (sem termo)
        List.fill(Random.between(10, 20))(randomNoiseWord()).mkString(" ")
    }
  }


  private def randomNoiseWord(): String = {
    extraNoiseWords(Random.nextInt(extraNoiseWords.length))
  }
}
