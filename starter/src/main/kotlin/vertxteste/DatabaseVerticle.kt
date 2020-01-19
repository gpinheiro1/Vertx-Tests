package vertxteste

import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Promise
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.pgclient.PgConnectOptions
import io.vertx.kotlin.sqlclient.PoolOptions
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.SqlConnection
import io.vertx.sqlclient.Tuple
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class DatabaseVerticle : AbstractVerticle() {

  lateinit var client: PgPool

  override fun start(startPromise: Promise<Void>) {

    val connectOptions = PgConnectOptions(
      port = 5432,
      host = "localhost",
      database = "postgres",
      user = "postgres",
      password = "postgres"
    )
    connectOptions.addProperty("search_path", "teste")

    val poolOptions = PoolOptions(
      maxSize = 5
    )

    client = PgPool.pool(vertx, connectOptions, poolOptions)

    vertx.eventBus().consumer<JsonObject>("com.movilepay.vertxteste.repository.insert", this::insertMessage)
    vertx.eventBus().consumer<JsonObject>("com.movilepay.vertxteste.repository.getAll", this::getMessages)
    vertx.eventBus().consumer<JsonObject>("com.movilepay.vertxteste.repository.deleteById", this::deleteMessageById)
    vertx.eventBus().consumer<JsonObject>("com.movilepay.vertxteste.repository.update", this::updateMessage)
    startPromise.complete()
  }

  private fun insertMessage(message: Message<JsonObject>) {

    client.getConnection { res ->
      if (res.succeeded()) {
        val connection = res.result()
        val query = "INSERT INTO vertx (id, message, number_characters, created_at) VALUES ($1, $2, $3, $4)"

        //recuperar os dados

        val stringId = message.body().getString("id")
        val id = UUID.fromString(stringId)
        val msg = message.body().getString("message")
        val msgLength = message.body().getInteger("message_length")
        val createdAt = LocalDateTime.ofInstant(message.body().getInstant("created_at"), ZoneId.of("UTC"))

        //organizar/preparar saída
        val tuple = Tuple.of(id, msg, msgLength, createdAt)

        connection.preparedQuery(query, tuple) { res2 ->
          if (res2.succeeded()) {
            res2.result()
            message.reply(JsonObject())
          } else {
            message.fail(0, res2.cause().message)
          }
        }
      } else {
        message.fail(0, "Something went wrong at DatabaseVerticle!")
      }
    }
  }

  private fun getMessages(message: Message<JsonObject>) {

    client.getConnection { res: AsyncResult<SqlConnection> ->
      if (res.succeeded()) {
        val connection = res.result()
        val query = "SELECT * FROM vertx"
        connection.query(query) { resultSet ->
          if (resultSet.succeeded()) {
            val result = JsonArray()
            resultSet.result().forEach {
              result.add(JsonObject().put("mensagem", it.getString("message")))
            }
            message.reply(result)
          } else {
            message.fail(0, resultSet.cause().message)
          }
        }
      }
    }
  }

  private fun deleteMessageById(message: Message<JsonObject>) {

    client.getConnection { res: AsyncResult<SqlConnection> ->
      val strUUID = message.body().getString("id")
      val uuid = try {
        UUID.fromString(strUUID)
      } catch (ex: IllegalArgumentException) {
        message.fail(-1, "Invalid uuid number")
      }

      if (res.succeeded()) {
        val connection = res.result()
        val query = "DELETE FROM vertx WHERE id = $1"
        connection.preparedQuery(query, Tuple.of(uuid)) { result ->
          if (result.succeeded()) {
            message.reply(JsonObject())
          } else {
            message.fail(0, result.cause().message)
          }
        }
      }
    }
  }


  fun updateMessage(message: Message<JsonObject>) {
    client.getConnection { res: AsyncResult<SqlConnection> ->
      if (res.succeeded()) {
        val connection = res.result()
        val query = "UPDATE vertx SET message = $1, number_characters = $2, updated_at = $3 WHERE id = $4"

        //recuperar os dados para passar na tupla

        val idStr = message.body().getString("id")
        val id = UUID.fromString(idStr)
        val msg = message.body().getString("message")
        val messageLength = message.body().getString("message_length")
        val updatedAt = LocalDateTime.ofInstant(message.body().getInstant("updated_at"), ZoneId.of("UTC"))

        val tuple = Tuple.of(msg, messageLength, updatedAt, id)

        connection.preparedQuery(query, tuple) { resultAsync ->
          if (resultAsync.succeeded()) {
            message.reply(JsonObject())
          } else {
            message.fail(0, resultAsync.cause().message)
          }
        }
      }
    }
  }


}
