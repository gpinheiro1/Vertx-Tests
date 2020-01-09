package vertxteste

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject

class IdadeVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    vertx.eventBus().consumer<JsonObject>("movilep.com") { message: Message<JsonObject> ->
      if (message.body().getInteger("idade") > 18) {
        message.reply(
          JsonObject()
            .put("mensagem", "${message.body().getString("nome")} ${message.body().getString("sobrenome")}, você é maior de idade")
        )
      } else {
        message.reply(
          JsonObject()
            .put("mensagem", "${message.body().getString("nome")} ${message.body().getString("sobrenome")}, você é menor de idade")
        )
      }
    }

    startPromise.complete()
  }
}

