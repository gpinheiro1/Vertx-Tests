package vertxteste

import io.vertx.core.Vertx

fun main() {
  val vertx: Vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle::class.java.name) { res ->
    if (res.succeeded()) {
      println("Aplicação no ar!")
    } else {
      println(res.cause())
    }
  }
}
