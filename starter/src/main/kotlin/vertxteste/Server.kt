package vertxteste

import io.vertx.core.Vertx

fun main() {
  val vertx: Vertx = Vertx.vertx()
  vertx.deployVerticle(ControllerVerticle::class.java.name) { res ->
    if (res.succeeded()) {
      println("ControllerVerticle no ar!")
    } else {
      println(res.cause())
    }
  }

  vertx.deployVerticle(MessageServiceVerticle::class.java.name) { res ->
    if (res.succeeded()) {
      println("MessageServiceVerticle no ar!")
    } else
      println(res.cause())
  }

  vertx.deployVerticle(DatabaseVerticle::class.java.name) { res ->
    if (res.succeeded()) {
      println("DatabaseVerticle no ar!")
    } else
      println(res.cause())
  }
}

