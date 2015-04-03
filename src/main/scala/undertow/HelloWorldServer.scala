package undertow

import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers

/**
 * Created by akhil on 4/3/15.
 */
object HelloWorldServer {
  def main(args: Array[String]) {
    val server: Undertow = Undertow.builder.addHttpListener(8080, "localhost").setHandler( new HttpHandler {
      @throws(classOf[Exception])
      def handleRequest(exchange: HttpServerExchange) {
        exchange.getResponseHeaders.put(Headers.CONTENT_TYPE, "text/plain")
        exchange.getResponseSender.send("PONG!")
      }
    }).build
    server.start
  }
}