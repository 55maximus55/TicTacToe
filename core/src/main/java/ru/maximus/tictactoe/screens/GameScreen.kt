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

    val view = table {
        setFillParent(true)

        table {
            textButton(text = "X", style = defaultStyle)
            textButton(text = "0", style = defaultStyle)
            textButton(text = "X", style = defaultStyle).cell(row = true)

            textButton(text = "O", style = defaultStyle)
            textButton(text = "X", style = defaultStyle)
            textButton(text = "O", style = defaultStyle).cell(row = true)

            textButton(text = "X", style = defaultStyle)
            textButton(text = "0", style = defaultStyle)
            textButton(text = "X", style = defaultStyle).cell(row = true)
        }
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
    }

    private fun removeEvents() {
        app.socket.off(EVENT_ROOMS_GET_KICK)
        app.socket.off(EVENT_ROOMS_PLAYER_JOINED)
        app.socket.off(EVENT_ROOMS_PLAYER_LEFT)
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
                add(Label(i, Scene2DSkin.defaultSkin, defaultStyle))
                row()
            }
        }
    }

}