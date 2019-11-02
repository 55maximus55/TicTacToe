package ru.maximus.tictactoe.server

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.corundumstudio.socketio.*
import org.json.JSONArray
import org.json.JSONObject
import ru.maximus.tictactoe.*
import ru.maximus.tictactoe.server.events.createConnectionEvents
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Server : Game() {

    lateinit var ioServer: SocketIOServer
    val playersMap = HashMap<UUID, Player>()
    val playersSearchingGames = ArrayList<UUID>()
    val roomsMap = HashMap<UUID, Room>()

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
                if (roomsMap.containsKey(it.sessionId)) {

                }
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

        // new game events
        ioServer.apply {
            addEventListener(PLAY_CREATE_GAME, String::class.java) { client, data, _ ->
                if (checkPlayerAuth(client)) {
                    if (playersMap[client.sessionId]!!.inGame) {
                        client.sendEvent(PLAY_CREATE_GAME_SUCCESS, false)
                    } else {
                        playersMap[client.sessionId]!!.gameId = client.sessionId
                        roomsMap[client.sessionId] = Room().apply {
                            players.add(playersMap[client.sessionId])
                        }

                        client.sendEvent(PLAY_CREATE_GAME_SUCCESS, true)
                    }
                } else {
                    client.sendEvent(PLAY_CREATE_GAME_SUCCESS, false)
                }
            }
            addEventListener(PLAY_GET_GAMES_LIST, String::class.java) { client, data, _ ->
                val o = JSONObject()
                o.put(GAME_COUNT, roomsMap.keys.size)
                for ((i, room) in roomsMap.keys.withIndex()) {
                    o.put(i.toString(), room.toString())
                }
                client.sendEvent(PLAY_SEND_GAMES_LIST, o.toString())
            }
        }

        ioServer.start()
        Gdx.app.log("SocketIO", "Server started")
    }

    fun checkPlayerAuth(client: SocketIOClient) : Boolean {
        return playersMap[client.sessionId]!!.dbId != -1
    }

    override fun render() {}

    override fun dispose() {
        ioServer.stop()
        Gdx.app.log("SocketIO", "Server closed")

        DB.closeDB()
    }
}
