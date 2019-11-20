package ru.maximus.tictactoe.server

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer

class Server : Game() {

    lateinit var ioServer: SocketIOServer

    override fun create() {
        ioServer = SocketIOServer(Configuration().apply {
            port = 7777
        })

        connectDisconnectEvents()

        ioServer.start()
        Gdx.app.log("TicTacToe", "Server started, port: ${ioServer.configuration.port}")
    }

    fun connectDisconnectEvents() {
        ioServer.addConnectListener {
            println(it.sessionId)
        }
    }

}
