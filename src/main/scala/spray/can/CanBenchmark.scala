/**
 * Created by akhil on 4/2/15.
 */
package spray.can

import akka.actor.{Props, ActorSystem}
import akka.io.IO

object Main extends App {

  implicit val system = ActorSystem()

  // the handler actor replies to incoming HttpRequests
  val handler = system.actorOf(Props[CanBenchmark], name = "handler")

  val interface = "127.0.0.1"//system.settings.config.getString("app.interface")
  val port = 8080 //system.settings.config.getInt("app.port")
  IO(Http) ! Http.Bind(handler, interface, port)
}

import scala.concurrent.duration._
import akka.actor.Actor
import spray.http._
import HttpMethods._
import StatusCodes._

class CanBenchmark extends Actor {
  import context.dispatcher // ExecutionContext for scheduler

  def receive = {

    // when a new connection comes in we register ourselves as the connection handler
    case _: Http.Connected => sender ! Http.Register(self)

    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) => sender ! HttpResponse(entity = "PONG!")

    case HttpRequest(GET, Uri.Path("/stop"), _, _, _) =>
      sender ! HttpResponse(entity = "Shutting down in 1 second ...")
      context.system.scheduler.scheduleOnce(1.second) { context.system.shutdown() }

    case _: HttpRequest => sender ! HttpResponse(NotFound, entity = "Unknown resource!")
  }
}
