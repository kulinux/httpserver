package e2e

import http.HttpServer
import sttp.client3._
import cats.effect.unsafe.implicits.global
import scala.concurrent.ExecutionContext.Implicits.global

import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import scala.concurrent.Future
import org.scalatest.BeforeAndAfterAll

class HttpTest extends AnyFeatureSpec with GivenWhenThen with Matchers {

  val Port = 8989

  def setupHttpServer() = {
    val httpServer = HttpServer(Port)
    val startServer = Future {
      httpServer.start().unsafeRunSync()
    }
    Thread.sleep(1000)
  }

  info("Http Server")

  feature("Bind Http Port") {

    scenario("response ok") {
      Given("Http server")
      setupHttpServer()

      When("I create a connection with port")
      val request = basicRequest.get(uri"http://127.0.0.1:$Port")
      val backend = HttpClientSyncBackend()
      val response = request.send(backend)

      Then("Connection is successfully open")
      response.isSuccess shouldBe true
    }

    scenario("bind http port") {
      Given("Http server")
      setupHttpServer()

      When("I create a connection with port")
      val request = basicRequest.get(uri"http://127.0.0.1:$Port/index.html")
      val backend = HttpClientSyncBackend()
      val response = request.send(backend)

      Then("Connection is not found")
      response.code.code shouldBe 400
    }
  }
}
