import cats.effect._
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s._
import server.controller.MockApi

object MockServer extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val httpApp: HttpApp[IO] = MockApi.routes.orNotFound

    EmberServerBuilder.default[IO]
      .withHost(host"localhost")
      .withPort(port"8090")
      .withHttpApp(httpApp)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}
