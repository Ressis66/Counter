package ru.httpcounter

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple{
  def run: IO[Unit] =
    HttpServer.server.use(_ => IO.never)

}
