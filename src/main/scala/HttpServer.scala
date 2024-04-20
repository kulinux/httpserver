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

  def app(socket: Socket): IO[Unit] =
    for {
      (is, os) <- openWriter(socket)
      read <- readAll(is)
      _ <- IO.println(read)
      _ <- writeHttpHello(os)
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
  def writeHttpHello(os: OutputStream): IO[Unit] = IO({
    os.write("HTTP/1.1 200 OK\r\n\r\n".getBytes())
  })
  def readAll(is: InputStream): IO[String] = IO({
    val bufferedReader =
      new BufferedReader(new InputStreamReader(is))
    var stop = false
    var res = ""
    while (!stop) {
      val line = bufferedReader.readLine()
      res += line + "\n"
      if (line.length() == 0) {
        stop = true
      }
    }
    res
  })
