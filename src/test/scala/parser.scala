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
      "Content-Length: 0"
    )

    val message = parseHttp(messageRaw)
    val actual = message.map(_.file()).getOrElse("bad file")

    actual shouldBe ("/")
  }

  "Parser should give an error wether a bad method line" in {
    val messageRaw = List(
      "GET/ HTTP/1.1",
      "Content-Length: 0"
    )

    val message = parseHttp(messageRaw)

    message.isLeft shouldBe true
  }

  "Parser should allow no header" in {
    val messageRaw = List(
      "GET / HTTP/1.1"
    )

    val message = parseHttp(messageRaw)
    val actual = message.map(_.file()).getOrElse("bad file")

    actual shouldBe "/"
  }
}
