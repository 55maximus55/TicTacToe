package ru.maximus.tictactoe.server

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.corundumstudio.socketio.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap


class Server : Game() {

    lateinit var ioServer: SocketIOServer
    val playersMap = HashMap<UUID, Player>()

    override fun create() {
        DB.connect()
        DB.createDB()

        val config = Configuration().apply {
            port = 7777
        }

        ioServer = SocketIOServer(config)

        ioServer.addConnectListener {
            Gdx.app.log("SocketIO", "Player connected\t(${it.sessionId})")
            playersMap[it.sessionId] = Player(it.sessionId)
        }
        ioServer.addDisconnectListener {
            Gdx.app.log("SocketIO", "Player disconnected\t(${it.sessionId})")
            playersMap.remove(it.sessionId)
        }

        ioServer.start()
        Gdx.app.log("SocketIO", "Server started")

        ioServer.addEventListener("authTry", String::class.java) { client, data, _ ->
            val authData = JSONObject(data)
            val id = DB.auth(authData.getString("login"), authData.getString("pass"))
            client.sendEvent("authSuccess", id != -1)
            playersMap[client.sessionId]!!.dbId = id
            Gdx.app.log("Auth", "id(${client.sessionId}), data$authData, ${if (id == -1) "Fail" else "Success"}")
        }
    }

    override fun render() {

    }

    override fun dispose() {
        ioServer.stop()
        Gdx.app.log("SocketIO", "Server closed")

        DB.closeDB()
    }
}
