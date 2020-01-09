package vertxteste

import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Promise
import io.vertx.core.eventbus.Message
import io.vertx.core.http.HttpServer
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    val router: Router = Router.router(vertx) //garanto que a rota suporta a leitura do body
    router.route().handler(BodyHandler.create())

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
        .setStatusCode(200)
        .end(
          JsonObject()
            .put("mensagem", "Hello, $nome")
            .toBuffer()
        )
    }

    router.post("/hello").handler { rc: RoutingContext ->
      val body: JsonObject = rc.bodyAsJson
      val nome = body.getString("nome")
      vertx.eventBus().request<JsonObject>("movilepay.com", nome) { res: AsyncResult<Message<JsonObject>> ->
        if (res.succeeded()) {
          rc.response()
            .putHeader("Content-Type", "application/json")
            .setStatusCode(200)
            .end(
              res.result().body().toBuffer()
            )
        } else {
          rc.response()
            .putHeader("Content-Type", "application/json")
            .setStatusCode(500)
            .end(
              res.cause().message
            )
        }
      }
    }

    router.post("/usuario").handler { rc: RoutingContext ->
      val body: JsonObject = rc.bodyAsJson
      val nome = body.getString("nome")
      val sobrenome = body.getString("sobrenome")
      val idade = body.getInteger("idade") //pq não consigo passar a idade nesse meu request?
      vertx.eventBus().request<JsonObject>("movilep.com", body) { res: AsyncResult<Message<JsonObject>> ->
        if(res.succeeded()) {
          rc.response()
            .putHeader("Content-Type", "application/json")
            .setStatusCode(200)
            .end(
              res.result().body().toBuffer()
            )
        } else {
          rc.response()
            .putHeader("Content-Type", "application/json")
            .setStatusCode(500)
            .end(
              res.cause().message
            )
        }
      }
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
