package vertxteste

import io.vertx.core.Vertx

fun main() {
  val vertx: Vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle::class.java.name) { res ->
    if (res.succeeded()) {
      println("MainVerticle no ar!")
    } else {
      println(res.cause())
    }
  }
  vertx.deployVerticle(MessageVerticle::class.java.name) { res ->
    if (res.succeeded()) {
      println("MessageVerticle no ar!")
    } else {
      println(res.cause())
    }
  }

}
