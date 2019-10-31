package ru.maximus.tictactoe.server

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.corundumstudio.socketio.*
import org.json.JSONObject


class Server : Game() {

    lateinit var ioServer: SocketIOServer

    override fun create() {
        DB.connect()
        DB.createDB()

        val config = Configuration().apply {
            port = 7777
        }

        ioServer = SocketIOServer(config)

        ioServer.addConnectListener {
            Gdx.app.log("SocketIO", "Player connected\t(${it.sessionId})")
        }
        ioServer.addDisconnectListener {
            Gdx.app.log("SocketIO", "Player disconnected\t(${it.sessionId})")
        }

        ioServer.start()
        Gdx.app.log("SocketIO", "Server started")

        ioServer.addEventListener("authTry", String::class.java) { client, data, _ ->
            val authData = JSONObject(data)
            var id = DB.auth(authData.getString("login"), authData.getString("pass"))
            client.sendEvent("authSuccess", id != -1)
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
