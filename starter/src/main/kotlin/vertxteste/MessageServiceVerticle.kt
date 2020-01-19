package vertxteste

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import java.time.Instant
import java.util.*

class MessageServiceVerticle : AbstractVerticle() {

  companion object {
    const val AGE_KEY = "idade"
    const val FIRST_NAME_KEY = "nome"
    const val LAST_NAME_KEY = "sobrenome"
  }

  override fun start(startPromise: Promise<Void>) {

    vertx.eventBus().consumer<JsonObject>("com.movilepay.vertxteste.service.insert") { message: Message<JsonObject> ->
      //recepcao dos dados do event bus
      val ageStatus = if (message.body().getInteger(AGE_KEY) > 18) "maior" else "menor"
      val firstName = message.body().getString(FIRST_NAME_KEY)
      val lastName = message.body().getString(LAST_NAME_KEY)

      //preparacao/producao dos dados
      val id = UUID.randomUUID()
      val msg = "$firstName $lastName. você é $ageStatus de idade"
      val msgLen = msg.length
      val createdAt = Instant.now()

      //montar o objeto de saida
      val msgJsonObj = JsonObject()
                        .put("id", id.toString())
                        .put("message", msg)
                        .put("message_length", msgLen)
                        .put("created_at", createdAt)

      vertx.eventBus().request<JsonObject>("com.movilepay.vertxteste.repository.insert", msgJsonObj) {
        if (it.succeeded()) {
          message.reply(msgJsonObj)
        } else {
          message.fail(0, it.cause().message)
        }
      }
    }

    vertx.eventBus().consumer<JsonObject>("com.movilepay.vertxteste.service.getAll") { message: Message<JsonObject> ->
      // exemplo herbert: println(message.body().getString("bla")) ---> montar mensagem no json que está sendo enviado na controller usando "to" para mapear
      vertx.eventBus().request<JsonArray>("com.movilepay.vertxteste.repository.getAll", JsonObject()) {
        if (it.succeeded()) {
          message.reply(it.result().body())
        } else {
          message.fail(1, it.cause().message)
        }
      }
    }

    //falta fazer validacao do id quando o usuario digitá-lo
    vertx.eventBus().consumer<JsonObject>("com.movilepay.vertxteste.service.deleteById") { message: Message<JsonObject> ->
      vertx.eventBus().request<JsonObject>("com.movilepay.vertxteste.repository.deleteById", message.body()) {
        if (it.succeeded()) {
          message.reply(it.result().body())
        } else {
          message.fail(1, it.cause().message)
        }
      }
    }

    vertx.eventBus().consumer<JsonObject>("com.movilepay.vertxteste.service.update") { message: Message<JsonObject> ->

      val updatedAt = Instant.now()
      val msg = message.body().toString()
      val msgLen = msg.length
      val msgJson = JsonObject()
                      .put("message_length", msgLen)
                      .put("updated_at", updatedAt)

      vertx.eventBus().request<JsonObject>("com.movilepay.vertxteste.repository.update", msgJson) {
        if (it.succeeded()) {
          message.reply(JsonObject())
        } else {
          message.fail(1, it.cause().message)
        }
      }
    }
    startPromise.complete()
  }
}
