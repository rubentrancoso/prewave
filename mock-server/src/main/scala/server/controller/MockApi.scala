package server.controller

import server.service.MockAlertGenerator
import server.service.MockTermGenerator
import cats.effect.IO
import org.http4s.*
import org.http4s.dsl.io.*
import org.http4s.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.server.Router

object MockApi {
  val routes: HttpRoutes[IO] = Router(
    "/" -> HttpRoutes.of[IO] {
      case GET -> Root / "adminInterface" / "api" / "testQueryTerm" =>
        Ok(MockTermGenerator.generateTerms(10).asJson)

      case GET -> Root / "adminInterface" / "api" / "testAlerts" =>
        Ok(MockAlertGenerator.generateAlerts(5).asJson)
    }
  )
}
