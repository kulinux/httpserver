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
    println("crating")
    openPort().use(ss =>
      for {
        socket <- acceptConnection(ss)
        (is, os) <- openWriter(socket)
        _ <- readAll(is)
        _ <- writeHttpHello(os)
        _ <- closeSocket(socket)
      } yield ()
    )

  def openPort(): Resource[IO, ServerSocket] =
    Resource.make[IO, ServerSocket](openSS())(closeSS)

  def openSS(): IO[ServerSocket] = IO(ServerSocket(port))
  def closeSS(ss: ServerSocket): IO[Unit] = IO(ss.close())
  def acceptConnection(ss: ServerSocket): IO[Socket] = IO(ss.accept())
  def openWriter(s: Socket): IO[(InputStream, OutputStream)] = IO(
    (s.getInputStream(), s.getOutputStream())
  )
  def writeHttpHello(os: OutputStream): IO[Unit] = IO({
    os.write("HTTP/1.1 200 OK\n\n".getBytes())
  })
  def readAll(is: InputStream): IO[Unit] = IO({
    val bufferedReader =
      new BufferedReader(new InputStreamReader(is))
    var stop = false
    while (!stop) {
      val line = bufferedReader.readLine()
      println("read from stream" + line)
      if (line.length() == 0) {
        stop = true
      }
    }
  })
  def closeSocket(s: Socket): IO[Unit] = IO(s.close())
