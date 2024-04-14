package e2e

import http.HttpServer
import sttp.client3._
import cats.effect.unsafe.implicits.global
import scala.concurrent.ExecutionContext.Implicits.global

import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.should.Matchers
import scala.concurrent.Future

class Http extends AnyFeatureSpec with GivenWhenThen with Matchers {

  val Port = 8989

  info("Http Server")

  feature("Bind Http Port") {
    scenario("bind http port") {
      Given("Http server")
      val httpServer = HttpServer(Port)
      val startServer = Future {
        httpServer.start().unsafeRunSync()
      }
      Thread.sleep(1000)

      When("I create a connection with port")
      val request = basicRequest.get(uri"http://127.0.0.1:$Port")
      val backend = HttpClientSyncBackend()
      val response = request.send(backend)

      Then("Connection is successfully open")
      response.isSuccess shouldBe true
    }
  }

}
