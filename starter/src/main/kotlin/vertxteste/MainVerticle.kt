package vertxteste

import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Promise
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    val router: Router = Router.router(vertx)
    router.get("/").handler { routingContext ->
      routingContext.response()
        .putHeader("Content-Type", "application/json")
        .setStatusCode(200).end(
          JsonObject()
            .put("nome", "Giovana")
            .put("idade", "23")
            .put("endereço", "francisco glicério")
            .toBuffer()
        )
    }
    router.get("/login").handler { routingContext: RoutingContext ->
      routingContext.response()
        .putHeader("Content-Type", "application/json")
        .setStatusCode(201)
        .end(
          JsonObject()
            .put("mensagem", "Olá, bem-vindo à tela de login =D ")
            .toBuffer()
        )
    }
    //pega um parâmetro do usuario por query string
    router.get("/hello").handler { routingContext: RoutingContext ->
      val nome: String = routingContext.request().params().get("nome")
      routingContext.response()
        .putHeader("Content-Type", "application/json")
        .setStatusCode(201)
        .end(
          JsonObject()
            .put("mensagem", "Olá, $nome =D ")
            .toBuffer()
        )
    }
    router.get("/hello/:nome").handler { rc: RoutingContext ->
      val nome: String = rc.request().getParam("nome")
      rc.response()
        .putHeader("Content-Type", "application/json")
        .setStatusCode(201)
        .end(
          JsonObject()
            .put("mensagem","Hello, $nome")
            .toBuffer()
        )
    }

    router.post("/hello").handler { rc: RoutingContext ->
      val nome: String = rc.bodyAsJson.getString("nome")

      rc.response()
        .putHeader("Content-Type", "application/json")
        .setStatusCode(201)
        .end(
          JsonObject()
            .put("mensagem", "Hello, $nome")
            .toBuffer()
        )
    }

    vertx
      .createHttpServer()
      .requestHandler(router::handle)
      .listen(8888) { http: AsyncResult<HttpServer> ->
        if (http.succeeded()) {
          startPromise.complete()
          println("HTTP server started on port 8888")
        } else {
          startPromise.fail(http.cause());
        }
      }
  }
}
