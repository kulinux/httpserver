package http

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import cats._
import cats.data._
import cats.syntax.all._

class HttpParserTest extends AnyFreeSpec with Matchers {
  "Parser should parse a correct http message" in {
    val messageRaw = List(
      "GET / HTTP/1.1",
      "Connection: Upgrade, HTTP2-Settings",
      "Content-Length: 0",
      "Host: 127.0.0.1:8989",
      "HTTP2-Settings: AAEAAEAAAAIAAAABAAMAAABkAAQBAAAAAAUAAEAA",
      "Upgrade: h2c",
      "User-Agent: Java-http-client/21.0.2",
      "Accept-Encoding: gzip, deflate"
    )

    val message = parseHttp(messageRaw)

    val actual = message.map(_.file()).getOrElse("bad file")

    actual shouldBe ("/")

  }
}
