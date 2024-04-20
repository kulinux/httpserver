package http

import cats._
import cats.data._
import cats.syntax.all._
import sttp.client3.RequestT

opaque type HttpRequest = List[String]

extension (self: HttpRequest)
  def file(): Either[String, String] = Either.right[String, String]("")

def parseHttp(raw: List[String]): Either[String, HttpRequest] = ???

case class StatusLine(status: Int, file: String)
case class Header(header: String, value: String)
case class Request(statusLine: StatusLine, headers: List[Header])

def parseStatusLine(raw: String): Either[String, StatusLine] = ???

def internalParseHttp(raw: List[String]): Either[String, Request] = for {
  head <- raw.headOption.toRight("No lines in header")
  statusLine <- parseStatusLine(head)
} yield Request(statusLine, List())
