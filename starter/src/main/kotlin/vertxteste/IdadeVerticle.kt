package vertxteste

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise

class IdadeVerticle: AbstractVerticle() {

  override fun start(startPromise: Promise<Void>?) {

    vertx.eventBus().consumer<String>("")
  }
}
