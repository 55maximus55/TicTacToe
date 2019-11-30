package ru.maximus.tictactoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.scene2d.*
import org.json.JSONObject
import ru.maximus.tictactoe.*

class GameScreen(val stage: Stage, val app: App) : KtxScreen {

    lateinit var playersTable: Table
    val playersList = ArrayList<String>()

    lateinit var gameTable: Table
    val gameList = ArrayList<Int>()

    val view = table {
        setFillParent(true)

        gameTable = table()
        table {
            textButton(text = "Exit", style = defaultStyle).cell(row = true).apply {
                onClick {
                    val jsonSend = JSONObject()
                    app.socket.emit(EVENT_ROOMS_LEAVE, jsonSend.toString())
                    app.setScreen<MainMenuScreen>()
                }
            }
            label(text = "Players:", style = defaultStyle).cell(row = true)
            scrollPane(style = defaultStyle) {
                playersTable = table()
            }
        }
    }

    override fun show() {
        stage.addActor(view)
        Gdx.input.inputProcessor = stage

        playersList.clear()
        updatePlayersTable()
        updatePlayersList()

        updateGameList()

        createEvents()
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun hide() {
        view.remove()

        removeEvents()
    }

    private fun createEvents() {
        app.socket.once(EVENT_ROOMS_GET_KICK) { data ->
            app.setScreen<MainMenuScreen>()
        }
        app.socket.on(EVENT_ROOMS_PLAYER_JOINED) { data ->
            val jsonGet = JSONObject(data[0].toString())
            playersList.add(jsonGet.getString(DATA_PLAYER_NAME))

            updatePlayersTable()
        }
        app.socket.on(EVENT_ROOMS_PLAYER_LEFT) { data ->
            val jsonGet = JSONObject(data[0].toString())
            playersList.remove(jsonGet.getString(DATA_PLAYER_NAME))

            updatePlayersTable()
        }

        createGameEvents()
    }

    private fun removeEvents() {
        app.socket.off(EVENT_ROOMS_GET_KICK)
        app.socket.off(EVENT_ROOMS_PLAYER_JOINED)
        app.socket.off(EVENT_ROOMS_PLAYER_LEFT)

        removeGameEvents()
    }

    private fun updatePlayersList() {
        app.socket.once(EVENT_ROOMS_GET_PLAYER_LIST) { data ->
            val jsonGet = JSONObject(data[0].toString())
            val n = jsonGet.getInt(DATA_LIST_COUNT)

            playersList.clear()
            for (i in 0 until n) {
                playersList.add(jsonGet.getString(i.toString()))
            }

            updatePlayersTable()
        }

        val jsonSend = JSONObject()
        app.socket.emit(EVENT_ROOMS_GET_PLAYER_LIST, jsonSend.toString())
    }

    private fun updatePlayersTable() {
        playersTable.apply {
            clear()
            for (i in playersList) {
                if (i == app.socket.id()) {
                    add(Label("($i)", Scene2DSkin.defaultSkin, defaultStyle))
                } else {
                    add(Label(i, Scene2DSkin.defaultSkin, defaultStyle))
                }
                row()
            }
        }
    }

    private fun updateGameList() {
        val jsonSend = JSONObject()
        app.socket.emit(EVENT_GAME_GET_CELLS, jsonSend.toString())
    }

    private fun updateGameTable() {
        gameTable.apply {
            clear()
            for (y in 0 until 3) {
                for (x in 0 until 3) {
                    val text = when (gameList[y * 3 + x]) {
                        DATA_CELL_EMPTY -> "#"
                        DATA_CELL_CROSS -> "X"
                        DATA_CELL_NOUGHT -> "O"
                        else -> ""
                    }
                    add(KTextButton(text = text, style = defaultStyle, skin = Scene2DSkin.defaultSkin).apply {
                        onClick {
                            makeMove(y * 3 + x)
                        }
                    })
                }
                row()
            }
        }
    }

    private fun createGameEvents() {
        app.socket.on(EVENT_GAME_GET_CELLS) { data ->
            val jsonGet = JSONObject(data[0].toString())
            val n = 0

            gameList.clear()
            for (i in 0 until 9) {
                gameList.add(jsonGet.getInt(i.toString()))
            }
            updateGameTable()
        }
        app.socket.on(EVENT_GAME_END) { data ->
            val jsonGet = JSONObject(data[0].toString())

            stage.addActor(KWindow("Game end", skin = Scene2DSkin.defaultSkin, style = defaultStyle).apply {
                textButton(when (jsonGet.getInt(DATA_GAME_END_STATE)) {
                    DATA_GAME_END_STATE_WIN -> "Win"
                    DATA_GAME_END_STATE_LOSE -> "Lose"
                    DATA_GAME_END_STATE_DRAW -> "Draw"
                    else -> ""
                }, style = defaultStyle)
                row()
                textButton(text = "Ok", style = defaultStyle).apply {
                    onClick {
                        parent.parent.removeActor(parent)
                    }
                }
            })
        }
    }

    private fun removeGameEvents() {
        app.socket.off(EVENT_GAME_GET_CELLS)
        app.socket.off(EVENT_GAME_END)
    }

    private fun makeMove(pos: Int) {
        val jsonSend = JSONObject()
        jsonSend.put(DATA_GAME_POS, pos)
        app.socket.emit(EVENT_GAME_MOVE, jsonSend.toString())
    }

}