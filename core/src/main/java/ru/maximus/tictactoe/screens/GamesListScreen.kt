package ru.maximus.tictactoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.scene2d.*
import org.json.JSONObject
import ru.maximus.tictactoe.*

class GamesListScreen(val stage: Stage, val app: App) : KtxScreen {

    lateinit var gameListTable: Table

    val view = table {
        setFillParent(true)

        table {
            table {
                textButton(text = "Back", style = defaultStyle).cell().apply {
                    onClick {
                        app.setScreen<MainMenuScreen>()
                    }
                }
                label(text = "Games", style = defaultStyle).cell(row = true)
            }.cell(row = true)
            scrollPane(style = defaultStyle) {
                gameListTable = table()
            }
        }.cell()
        table {
            textButton(text = "Create", style = defaultStyle).cell(row = true).apply {
                onClick {
                    createRoom()
                }
            }
            textButton(text = "Update", style = defaultStyle).cell(row = true).apply {
                onClick {
                    getGamesList()
                }
            }
        }.cell()
    }

    override fun show() {
        stage.addActor(view)
        Gdx.input.inputProcessor = stage

        getGamesList()
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun hide() {
        view.remove()
    }

    private fun getGamesList() {
        app.socket.once(EVENT_ROOMS_GET_LIST) { data ->
            val jsonGet = JSONObject(data[0].toString())
            val n = jsonGet.getInt(DATA_LIST_COUNT)
            gameListTable.apply {
                clear()
                for (i in 0 until n) {
                    add(KTextButton(text = jsonGet.getString(i.toString()), style = defaultStyle, skin = Scene2DSkin.defaultSkin).apply {
                        onClick {
                            joinRoom(jsonGet.getString(i.toString()))
                        }
                    })
                    row()
                }
            }
        }

        val jsonSend = JSONObject()
        app.socket.emit(EVENT_ROOMS_GET_LIST, jsonSend.toString())
    }

    private fun joinRoom(roomID : String) {
        app.socket.once(EVENT_ROOMS_JOIN_SUCCESS) { args ->
            val jsonGet = JSONObject(args[0].toString())
            if (jsonGet.getBoolean(DATA_SUCCESS)) {
                app.setScreen<GameScreen>()
            }
        }

        val jsonSend = JSONObject()
        jsonSend.put(DATA_ROOMS_JOIN_ROOMID, roomID)
        app.socket.emit(EVENT_ROOMS_JOIN, jsonSend.toString())
    }

    private fun createRoom() {
        app.socket.once(EVENT_ROOMS_CREATE_SUCCESS) { args ->
            val jsonGet = JSONObject(args[0].toString())
            if (jsonGet.getBoolean(DATA_SUCCESS)) {
                app.setScreen<GameScreen>()
            }
        }

        val jsonSend = JSONObject()
        app.socket.emit(EVENT_ROOMS_CREATE, jsonSend.toString())
    }

}