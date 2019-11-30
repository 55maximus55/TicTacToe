package ru.maximus.tictactoe.server

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import org.json.JSONObject
import ru.maximus.tictactoe.*
import java.lang.Exception
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

        connectionEvents()
        gameListScreenEvents()
        gameScreenEvents()

        ioServer.start()
        Gdx.app.log("TicTacToe", "Server started, port: ${ioServer.configuration.port}")
    }

    fun connectionEvents() {
        ioServer.addConnectListener { client ->
            players[client.sessionId] = Player()
            Gdx.app.log("Connection", "Player(${client.sessionId}) connected")
        }
        ioServer.addDisconnectListener { client ->
            if (players[client.sessionId]!!.inGame) {
                Gdx.app.log("Rooms", "Player(${client.sessionId}) left Room(${players[client.sessionId]!!.roomID})")
                players[client.sessionId]!!.inGame = false

                if (rooms.containsKey(client.sessionId)) {
                    Gdx.app.log("Rooms", "Room leader left Room(${players[client.sessionId]!!.roomID}), room removed")
                    for (i in rooms[client.sessionId]!!.players) {
                        removePlayerFromRoom(i)
                    }
                    rooms.remove(client.sessionId)
                } else {
                    Gdx.app.log("Rooms", "Player(${client.sessionId}) left room(${players[client.sessionId]!!.roomID})")
                    removePlayerFromRoom(client.sessionId)
                    rooms[players[client.sessionId]!!.roomID]!!.players.remove(client.sessionId)
                    val jsonSend = JSONObject().apply {
                        put(DATA_PLAYER_NAME, client.sessionId)
                    }
                    for (i in rooms[players[client.sessionId]!!.roomID]!!.players) {
                        ioServer.getClient(i).sendEvent(EVENT_ROOMS_PLAYER_LEFT, jsonSend.toString())
                    }
                }
            }
            Gdx.app.log("Connection", "Player(${client.sessionId}) disconnected")
//            Gdx.app.log("", "")
        }
    }

    fun gameListScreenEvents() {
        ioServer.addEventListener(EVENT_ROOMS_GET_LIST, String::class.java) { client, data, _ ->
            val jsonGet = JSONObject(data)
            val jsonSend = JSONObject()

            jsonSend.apply {
                put(DATA_LIST_COUNT, rooms.keys.size)
                for ((i, roomID) in rooms.keys.withIndex()) {
                    put(i.toString(), roomID.toString())
                }
            }
            client.sendEvent(EVENT_ROOMS_GET_LIST, jsonSend.toString())
        }
        ioServer.addEventListener(EVENT_ROOMS_JOIN, String::class.java) { client, data, _ ->
            val jsonGet = JSONObject(data)
            val jsonSend = JSONObject()

            if (players[client.sessionId]!!.inGame) {
                jsonSend.put(DATA_SUCCESS, false)
            } else {
                val roomID = UUID.fromString(jsonGet.getString(DATA_ROOMS_JOIN_ROOMID))
                if (rooms.keys.contains(roomID)) {
                    if (rooms[roomID]!!.players.size <= rooms[roomID]!!.maxPlayer - 1) {
                        rooms[roomID]!!.apply {
                            val jsonSendOthers = JSONObject().apply {
                                put(DATA_PLAYER_NAME, client.sessionId)
                            }
                            val stringSend = jsonSendOthers.toString()
                            for (i in players) {
                                ioServer.getClient(i).sendEvent(EVENT_ROOMS_PLAYER_JOINED, stringSend)
                            }

                            players.add(client.sessionId)
                        }
                        players[client.sessionId]!!.inGame = true
                        players[client.sessionId]!!.roomID = roomID

                        jsonSend.put(DATA_SUCCESS, true)
                        Gdx.app.log("Rooms", "Player(${client.sessionId}) joined to Room(${roomID})")
                    } else {
                        jsonSend.put(DATA_SUCCESS, false)
                    }
                } else {
                    jsonSend.put(DATA_SUCCESS, false)
                }
            }
            client.sendEvent(EVENT_ROOMS_JOIN_SUCCESS, jsonSend.toString())
        }
        ioServer.addEventListener(EVENT_ROOMS_CREATE, String::class.java) { client, data, _ ->
            val jsonGet = JSONObject(data)
            val jsonSend = JSONObject()

            if (players[client.sessionId]!!.inGame) {
                jsonSend.put(DATA_SUCCESS, false)
            } else {
                rooms[client.sessionId] = Room()
                rooms[client.sessionId]!!.players.add(client.sessionId)

                players[client.sessionId]!!.inGame = true
                players[client.sessionId]!!.roomID = client.sessionId

                jsonSend.put(DATA_SUCCESS, true)
                Gdx.app.log("Rooms", "Player(${client.sessionId}) created room")
            }
            client.sendEvent(EVENT_ROOMS_CREATE_SUCCESS, jsonSend.toString())
        }
    }

    fun gameScreenEvents() {
        ioServer.addEventListener(EVENT_ROOMS_GET_PLAYER_LIST, String::class.java) { client, data, _ ->
            val jsonGet = JSONObject(data.toString())
            val jsonSend = JSONObject()

            if (players[client.sessionId]!!.inGame) {
                val roomID = players[client.sessionId]!!.roomID
                if (rooms.containsKey(roomID)) {
                    jsonSend.apply {
                        put(DATA_LIST_COUNT, rooms[roomID]!!.players.size)
                        for ((i, playerID) in rooms[roomID]!!.players.withIndex()) {
                            put(i.toString(), playerID.toString())
                        }
                    }
                } else {
                    jsonSend.put(DATA_LIST_COUNT, 0)
                }
            } else {
                jsonSend.put(DATA_LIST_COUNT, 0)
            }

            client.sendEvent(EVENT_ROOMS_GET_PLAYER_LIST, jsonSend.toString())
        }
        ioServer.addEventListener(EVENT_ROOMS_LEAVE, String::class.java) { client, data, _ ->
            if (players[client.sessionId]!!.inGame) {
                players[client.sessionId]!!.inGame = false

                if (rooms.containsKey(client.sessionId)) {
                    Gdx.app.log("Rooms", "Room leader left Room(${players[client.sessionId]!!.roomID}), room removed")
                    for (i in rooms[client.sessionId]!!.players) {
                        removePlayerFromRoom(i)
                    }
                    rooms.remove(client.sessionId)
                } else {
                    Gdx.app.log("Rooms", "Player(${client.sessionId}) left room(${players[client.sessionId]!!.roomID})")
                    removePlayerFromRoom(client.sessionId)
                    rooms[players[client.sessionId]!!.roomID]!!.players.remove(client.sessionId)
                    val jsonSend = JSONObject().apply {
                        put(DATA_PLAYER_NAME, client.sessionId)
                    }
                    for (i in rooms[players[client.sessionId]!!.roomID]!!.players) {
                        ioServer.getClient(i).sendEvent(EVENT_ROOMS_PLAYER_LEFT, jsonSend.toString())
                    }
                }
            }
        }

        ioServer.addEventListener(EVENT_GAME_GET_CELLS, String::class.java) { client, data, _ ->
            val jsonGet = JSONObject(data)
            sendCellsToPlayer(client.sessionId)
        }
        ioServer.addEventListener(EVENT_GAME_MOVE, String::class.java) { client, data, _ ->
            val jsonGet = JSONObject(data)
            val jsonSend = JSONObject()

            if (rooms.containsKey(players[client.sessionId]!!.roomID)) {
                rooms[players[client.sessionId]!!.roomID]!!.apply {
                    val isFirst = players[0] == client.sessionId

                    var isMove = false

                    if (firstPlayerCross) {
                        if (isFirst && isCross) {
                            isMove = true
                        }
                        if (!isFirst && !isCross) {
                            isMove = true
                        }
                    } else {
                        if (isFirst && !isCross) {
                            isMove = true
                        }
                        if (!isFirst && isCross) {
                            isMove = true
                        }
                    }

                    if (isMove) {
                        if (cells[jsonGet.getInt(DATA_GAME_POS)] == DATA_CELL_EMPTY) {
                            cells[jsonGet.getInt(DATA_GAME_POS)] = if (isCross) DATA_CELL_CROSS else DATA_CELL_NOUGHT
                            isCross = !isCross

                            for (i in players) {
                                sendCellsToPlayer(i)
                            }
                        }
                    }

                    var crossWin = false
                    var noughtWin = false
                    var draw = true

                    //cross win check
                    apply {
                        for (y in 0 until 3) {
                            var t = true
                            for (x in 0 until 3) {
                                if (cells[y * 3 + x] != DATA_CELL_CROSS) {
                                    t = false
                                }
                            }
                            if (t) {
                                crossWin = true
                                break
                            }
                        }
                        for (x in 0 until 3) {
                            var t = true
                            for (y in 0 until 3) {
                                if (cells[y * 3 + x] != DATA_CELL_CROSS) {
                                    t = false
                                }
                            }
                            if (t) {
                                crossWin = true
                                break
                            }
                        }
                        for (i in 0 until 3) {
                            var t = true
                            if (cells[i * 4] != DATA_CELL_CROSS) {
                                t = false
                            }
                            if (t) {
                                crossWin = true
                                break
                            }
                        }
                        for (i in 0 until 3) {
                            var t = true
                            if (cells[i * 2 + 2] != DATA_CELL_CROSS) {
                                t = false
                            }
                            if (t) {
                                crossWin = true
                                break
                            }
                        }
                    }
                    //nought win check
                    apply {
                        for (y in 0 until 3) {
                            var t = true
                            for (x in 0 until 3) {
                                if (cells[y * 3 + x] != DATA_CELL_NOUGHT) {
                                    t = false
                                }
                            }
                            if (t) {
                                noughtWin = true
                                break
                            }
                        }
                        for (x in 0 until 3) {
                            var t = true
                            for (y in 0 until 3) {
                                if (cells[y * 3 + x] != DATA_CELL_NOUGHT) {
                                    t = false
                                }
                            }
                            if (t) {
                                noughtWin = true
                                break
                            }
                        }
                        for (i in 0 until 3) {
                            var t = true
                            if (cells[i * 4] != DATA_CELL_NOUGHT) {
                                t = false
                            }
                            if (t) {
                                noughtWin = true
                                break
                            }
                        }
                        for (i in 0 until 3) {
                            var t = true
                            if (cells[i * 2 + 2] != DATA_CELL_NOUGHT) {
                                t = false
                            }
                            if (t) {
                                noughtWin = true
                                break
                            }
                        }
                    }
                    //draw check
                    apply {
                        for (i in 0 until 9) {
                            if (cells[i] == DATA_CELL_EMPTY) {
                                draw = false
                            }
                        }
                        if (crossWin || noughtWin) {
                            draw = false
                        }
                    }

                    if (crossWin || noughtWin || draw) {
                        for (i in 0 until 9) {
                            cells[i] = DATA_CELL_EMPTY
                        }
                        for (i in players) {
                            sendCellsToPlayer(i)
                        }

                        isCross = true
                        firstPlayerCross = !firstPlayerCross
                    }
                }
            }
        }
    }

    fun sendCellsToPlayer(id: UUID) {
        val jsonSend = JSONObject()

        if (rooms.containsKey(players[id]!!.roomID)) {
            rooms[players[id]!!.roomID]!!.apply {
                for (i in 0 until 9) {
                    jsonSend.put(i.toString(), cells[i])
                }
            }
        }

        ioServer.getClient(id).sendEvent(EVENT_GAME_GET_CELLS, jsonSend.toString())
    }

    fun removePlayerFromRoom(id: UUID) {
        if (players[id]!!.inGame) {
            rooms[players[id]!!.roomID]!!.players.remove(id)
            players[id]!!.inGame = false
            ioServer.getClient(id).sendEvent(EVENT_ROOMS_GET_KICK)
        }
    }

}
