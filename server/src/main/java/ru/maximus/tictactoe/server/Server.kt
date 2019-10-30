package ru.maximus.tictactoe.server

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.corundumstudio.socketio.*
import com.corundumstudio.socketio.protocol.JacksonJsonSupport
import org.json.JSONObject
import ru.maximus.tictactoe.AuthData
import com.corundumstudio.socketio.listener.DataListener


class Server : Game() {

    lateinit var ioServer: SocketIOServer

    override fun create() {
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
            println(authData)
            client.sendEvent("authSuccess", true)
        }
    }

    override fun render() {

    }

    override fun dispose() {
        ioServer.stop()
        Gdx.app.log("SocketIO", "Server closed")
    }
}
