package akkahttp.can

/**
 * Created by akhil on 4/2/15.
 */

import akka.actor.ActorSystem
import akka.http.Http
import akka.http.model._
import akka.http.model.HttpMethods._
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._

import scala.concurrent.Future

object Main extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorFlowMaterializer()

    val requestHandler: HttpRequest => HttpResponse = {
      case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
        HttpResponse(
          entity = HttpEntity(MediaTypes.`text/html`,
            "<html><body>Hello world!</body></html>"))

      case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
        HttpResponse(entity = "PONG!")

      case HttpRequest(GET, Uri.Path("/crash"), _, _, _) =>
        sys.error("BOOM!")

      case _: HttpRequest =>
        HttpResponse(404, entity = "Unknown resource!")
    }

  val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
    Http(system).bind(interface = "localhost", port = 8080)

  val bindingFuture: Future[Http.ServerBinding] = serverSource.to(Sink.foreach { connection =>
    //println("Accepted new connection from " + connection.remoteAddress)

    connection handleWithSyncHandler requestHandler
    // this is equivalent to
    // connection handleWith { Flow[HttpRequest] map requestHandler }
  }).run()
}