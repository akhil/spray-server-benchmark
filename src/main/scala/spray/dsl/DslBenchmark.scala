package spray.dsl
import akka.actor.ActorSystem
import spray.http.StatusCodes
import spray.routing.SimpleRoutingApp

/**
 * Created by akhil on 4/2/15.
 */
object DslBenchmark extends App with SimpleRoutingApp {
  implicit val system = ActorSystem("my-system")

  startServer(interface = "localhost", port = 8080) {
    path("ping") {
      get {
        complete { "PONG!" }
      }
    }/* ~ path("hello") {
      get {
        complete { "HELLO!" }
      }
    }*/
  }
}
