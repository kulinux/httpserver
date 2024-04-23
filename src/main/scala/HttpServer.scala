package http

import cats.effect.IO
import cats.effect.kernel.Resource
import java.net.ServerSocket
import scala.io.Source
import java.net.Socket
import java.io.OutputStream
import java.io.InputStream
import java.io.BufferedReader
import java.io.InputStreamReader

class HttpServer(val port: Int):

  def start(): IO[Unit] =
    val rSocket = for {
      server <- openPort(port)
      socket <- openSocket(server)
    } yield socket

    rSocket
      .use(socket => app(socket))
      .handleError(err => err.printStackTrace())

  def app(socket: Socket): IO[Unit] =
    for {
      (is, os) <- openWriter(socket)
      read <- readAll(is)
      _ <- IO.println(read)
      request = parseHttp(read)
      _ <- writeHttpHello(os, request)
    } yield ()

  def openPort(port: Int): Resource[IO, ServerSocket] =
    def openSS(port: Int): IO[ServerSocket] = IO(ServerSocket(port))
    def closeSS(ss: ServerSocket): IO[Unit] = IO(ss.close())
    Resource.make[IO, ServerSocket](openSS(port))(closeSS)

  def openSocket(ss: ServerSocket): Resource[IO, Socket] =
    def acceptConnection(ss: ServerSocket): IO[Socket] = IO(ss.accept())
    def closeSocket(s: Socket): IO[Unit] = IO(s.close())
    Resource.make[IO, Socket](acceptConnection(ss))(
      closeSocket
    )

  def openWriter(s: Socket): IO[(InputStream, OutputStream)] = IO(
    (s.getInputStream(), s.getOutputStream())
  )

  def writeHttpHello(
      os: OutputStream,
      request: Either[String, HttpRequest]
  ): IO[Unit] =
    val file = request.map(_.file())
    if (file.isLeft) return IO.raiseError(Throwable(file.left.getOrElse("ok")))
    val status = if (file.getOrElse("not found") == "/") 200 else 400

    IO({
      os.write(s"HTTP/1.1 $status OK\r\n\r\n".getBytes())
    })

  def readAll(is: InputStream): IO[List[String]] = IO({
    val bufferedReader =
      new BufferedReader(new InputStreamReader(is))
    var stop = false
    var res = List[String]()
    while (!stop) {
      val line = bufferedReader.readLine()
      res = res :+ line
      if (line.length() == 0) {
        stop = true
      }
    }
    res
  })
