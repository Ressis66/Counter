package ru.httpcounter

import cats.effect.IO
import com.comcast.ip4s.{Host, Port}
import fs2.Stream
import io.circe.literal._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router

import scala.concurrent.duration._


object HttpServer {
  val serviceOne: HttpRoutes[IO] =
    HttpRoutes.of { case GET -> Root / "slow" / chunk / total / time =>
      Ok(chunkFirst(chunk, total, time))
    }

  val serviceTwo: HttpRoutes[IO] =
    HttpRoutes.of { case GET -> Root / "counter" =>
      counterOne = counterOne + 1
       Ok(json"""{"Counter": $counterOne}""")

    }
  var counterOne =  0

  def chunkFirst(chunk: String, total: String, time: String) = {
    val duration = FiniteDuration(Duration(time).toSeconds, SECONDS)
    Stream(5).repeat.delayBy[IO](duration).map(x => x.toByte)
      .chunkN(chunk.toInt).take(total.toInt).attempt.compile.drain
  }
  val router = Router("/" -> serviceOne, "/api" -> serviceTwo)

  val server = for {
    s <- EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(8080).get)
      .withHost(Host.fromString("localhost").get)
      .withHttpApp(router.orNotFound)
      .build
  } yield s




}
