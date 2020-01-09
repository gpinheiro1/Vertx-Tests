package vertxteste

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject

class MessageVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    vertx.eventBus().consumer<String>("movilepay.com") { message: Message<String> ->
      message.reply(
        JsonObject()
          .put("mensagem", "Hello, ${message.body()}")
      )
    }
    startPromise.complete()
  }

}

