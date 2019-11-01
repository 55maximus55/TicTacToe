package ru.maximus.tictactoe.server

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.corundumstudio.socketio.*
import org.json.JSONObject
import ru.maximus.tictactoe.*
import ru.maximus.tictactoe.server.events.createConnectionEvents
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

        // player connect/disconnect events
        ioServer.apply {
            addConnectListener {
                Gdx.app.log("SocketIO", "Player connected\t(${it.sessionId})")
                playersMap[it.sessionId] = Player(it.sessionId)
            }
            addDisconnectListener {
                Gdx.app.log("SocketIO", "Player disconnected\t(${it.sessionId})")
                playersMap.remove(it.sessionId)
            }
        }

        // auth events
        ioServer.apply {
            addEventListener(AUTH_TRY, String::class.java) { client, data, _ ->
                val authData = JSONObject(data)
                val id = DB.auth(authData.getString(AUTH_DATA_USERNAME), authData.getString(AUTH_DATA_PASSWORD))
                client.sendEvent(AUTH_SUCCESS, id != -1)
                playersMap[client.sessionId]!!.dbId = id
                Gdx.app.log("Auth", "id(${client.sessionId}), data$authData, ${if (id == -1) "Fail" else "Success"}")
            }
            addEventListener(REG_TRY, String::class.java) { client, data, _ ->
                val regData = JSONObject(data)
                val success = DB.reg(regData.getString(AUTH_DATA_USERNAME), regData.getString(AUTH_DATA_PASSWORD))
                client.sendEvent(REG_SUCCESS, success)
                Gdx.app.log("Register", "id(${client.sessionId}), data$regData, ${if (success) "Success" else "Fail"}")
            }
        }

        ioServer.start()
        Gdx.app.log("SocketIO", "Server started")
    }

    override fun render() {}

    override fun dispose() {
        ioServer.stop()
        Gdx.app.log("SocketIO", "Server closed")

        DB.closeDB()
    }
}
