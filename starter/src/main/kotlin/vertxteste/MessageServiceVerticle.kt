package vertxteste

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject

class MessageServiceVerticle : AbstractVerticle() {

  companion object {
    const val AGE_KEY = "idade"
    const val FIRST_NAME_KEY = "nome"
    const val LAST_NAME_KEY = "sobrenome"
  }

  override fun start(startPromise: Promise<Void>) {

    vertx.eventBus().consumer<JsonObject>("com.movilepay.vertxteste.service.insert") { message: Message<JsonObject> ->
      val ageStatus = if (message.body().getInteger(AGE_KEY) > 18) "maior" else "menor"
      val firstName = message.body().getString(FIRST_NAME_KEY)
      val lastName = message.body().getString(LAST_NAME_KEY)

      val messageObject = JsonObject().put("mensagem", "$firstName $lastName, você é $ageStatus de idade")

      vertx.eventBus().request<JsonObject>("com.movilepay.vertxteste.repository.insert", messageObject) {
        if (it.succeeded()) {
          message.reply(messageObject)
        } else {
          message.fail(0, it.cause().message)
        }
      }
    }

    vertx.eventBus().consumer<JsonObject>("com.movilepay.vertxteste.service.getAll") { message: Message<JsonObject> ->
     // exemplo herbert: println(message.body().getString("bla")) ---> montar mensagem no json que está sendo enviado na controller usando "to" para mapear
      vertx.eventBus().request<JsonArray>("com.movilepay.vertxteste.repository.getAll", JsonObject()) {
        if (it.succeeded()){
          message.reply(it.result().body())
        } else {
          message.fail(1, it.cause().message)
        }
      }
    }
    vertx.eventBus().consumer<JsonObject>("com.movilepay.vertxteste.service.deleteById") { message: Message<JsonObject> ->
        val id = message.body().getString("id")
      vertx.eventBus().request<JsonObject>("com.movilepay.vertxteste.repository.deleteById", JsonObject()) {
        if(it.succeeded()){
          message.reply(it.result().body())
        } else {
          message.fail(1, it.cause().message)
        }
      }

    }
    startPromise.complete()
  }
}
