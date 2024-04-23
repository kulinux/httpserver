package http

import cats._
import cats.data._
import cats.syntax.all._
import sttp.client3.RequestT

opaque type HttpRequest = Request

extension (self: HttpRequest) def file(): String = self.method.file

def parseHttp(raw: List[String]): Either[String, HttpRequest] =
  internalParseHttp(raw)

case class MethodLine(method: String, file: String)
case class Header(header: String, value: String)
case class Request(method: MethodLine, headers: List[Header])

def parseMethodLine(raw: String): Either[String, MethodLine] =
  val tokens = raw.split(" ")
  if (tokens.length != 3) return s"Bad method line $raw".asLeft

  val Array(method, file, _) = tokens

  MethodLine(method, file).asRight

def internalParseHttp(raw: List[String]): Either[String, Request] = for {
  head <- raw.headOption.toRight("No lines in header")
  statusLine <- parseMethodLine(head)
} yield Request(statusLine, List())
