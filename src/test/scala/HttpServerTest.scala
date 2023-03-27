import cats.data.OptionT
import cats.effect.IO
import org.http4s.{Method, Response}
import org.http4s.Uri.uri
import ru.httpcounter.HttpServer

object HttpServerTest {
  val request = org.http4s.Request.apply(Method.GET, uri("/api/counter"))
  val response: OptionT[IO, Response[IO]] =  HttpServer.serviceTwo.run(request)
  response.show =="""{"Counter":"1"}"""
}
