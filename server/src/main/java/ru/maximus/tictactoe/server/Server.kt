package ru.maximus.tictactoe.server

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import org.json.JSONObject
import ru.maximus.tictactoe.*
import java.util.*
import kotlin.collections.HashMap

class Server : Game() {

    lateinit var ioServer: SocketIOServer
    var rooms = HashMap<UUID, Room>()
    var players = HashMap<UUID, Player>()

    override fun create() {
        ioServer = SocketIOServer(Configuration().apply {
            port = 7777
        })

        connectDisconnectEvents()
        roomEvents()

        ioServer.start()
        Gdx.app.log("TicTacToe", "Server started, port: ${ioServer.configuration.port}")
    }

    fun connectDisconnectEvents() {
        ioServer.addConnectListener {
            players[it.sessionId] = Player()
            Gdx.app.log("Connection", "Player connected (${it.sessionId})")
        }
        ioServer.addDisconnectListener {
            players.remove(it.sessionId)
            Gdx.app.log("Connection", "Player disconnected (${it.sessionId})")
            if (rooms.containsKey(it.sessionId)) {
                for (i in 1 until rooms[it.sessionId]!!.players.size) {
                    ioServer.getClient(rooms[it.sessionId]!!.players[i]).sendEvent(EVENT_ROOMS_KICK)
                }
                rooms.remove(it.sessionId)
                Gdx.app.log("Rooms", "Room leader disconnected, room removed (${it.sessionId})")
            }
        }
    }

    fun roomEvents() {
        ioServer.addEventListener(EVENT_ROOMS_CREATE, String::class.java) { client, data, _ ->
            if (!rooms.containsKey(client.sessionId)) {
                rooms[client.sessionId] = Room()
                rooms[client.sessionId]!!.players.add(client.sessionId)

                players[client.sessionId]!!.inGame = true
                players[client.sessionId]!!.gameId = client.sessionId
                Gdx.app.log("Rooms", "Created room (${client.sessionId})")
            }
        }
        ioServer.addEventListener(EVENT_ROOMS_LEAVE, String::class.java) { client, data, _ ->
            if (rooms.containsKey(client.sessionId)) {
                for (i in 0 until rooms[client.sessionId]!!.players.size) {
                    kickPlayerFromRoom(rooms[client.sessionId]!!.players[i])
                }
                rooms.remove(client.sessionId)
                Gdx.app.log("Rooms", "Room leader left, room removed (${client.sessionId})")
            }
        }
        ioServer.addEventListener(EVENT_ROOMS_GET_LIST, String::class.java) { client, data, _ ->
            val list = JSONObject()
            list.apply {
                put(DATA_ROOMS_GET_LIST_COUNT, rooms.keys.size)
                for ((i, roomID) in rooms.keys.withIndex()) {
                    put(i.toString(), roomID.toString())
                }
            }
            client.sendEvent(EVENT_ROOMS_GET_LIST, list.toString())
        }
        ioServer.addEventListener(EVENT_ROOMS_GET_PLAYER_LIST, String::class.java) { client, data, _ ->
            val json = JSONObject(data.toString())
            val roomID = UUID.fromString(json.getString(DATA_ROOMS_JOIN_ROOMID))

            val list = JSONObject()
            if (rooms.containsKey(roomID)) {
                list.apply {
                    put(DATA_ROOMS_PLAYERS_GET_LIST_COUNT, rooms[roomID]!!.players.size)
                    for ((i, playerID) in rooms[roomID]!!.players.withIndex()) {
                        put(i.toString(), playerID.toString())
                    }
                }
            } else {
                list.put(DATA_ROOMS_PLAYERS_GET_LIST_COUNT, 0)
            }
            client.sendEvent(EVENT_ROOMS_GET_PLAYER_LIST, list.toString())
        }
        ioServer.addEventListener(EVENT_ROOMS_JOIN_TO_ROOM, String::class.java) { client, data, _ ->
            if (!players[client.sessionId]!!.inGame) {
                val json = JSONObject(data.toString())
                val roomID = UUID.fromString(json.getString(DATA_ROOMS_JOIN_ROOMID))

                if (rooms.containsKey(roomID)) {
                    rooms[roomID]!!.apply {
                        val jsonSend = JSONObject()
                        jsonSend.put(DATA_ROOMS_JOINED_PLAYER_NAME, client.sessionId)
                        for (i in players) {
                            ioServer.getClient(i).sendEvent(EVENT_ROOMS_PLAYER_JOINED, jsonSend.toString())
                        }
                        players.add(client.sessionId)
                    }

                    Gdx.app.log("Rooms", "Player (${client.sessionId}) joined to room (${roomID})")
                    println(players)
                }
            }
        }
    }

    fun kickPlayerFromRoom(id : UUID) {
        ioServer.getClient(id).sendEvent(EVENT_ROOMS_KICK)
        players[id]!!.inGame = false
    }

}
