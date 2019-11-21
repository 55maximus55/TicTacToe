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
                    app.socket.emit(EVENT_ROOMS_CREATE)
                    app.setScreen<GameScreen>()
                }
            }
        }.cell()
    }

    override fun show() {
        stage.addActor(view)
        Gdx.input.inputProcessor = stage

        app.socket.once(EVENT_ROOMS_GET_LIST) { data ->
            val d = JSONObject(data[0].toString())
            val n = d.getInt(DATA_ROOMS_GET_LIST_COUNT)
            gameListTable.apply {
                clear()
                for (i in 0 until n) {
                    add(KTextButton(text = d.getString(i.toString()), style = defaultStyle, skin = Scene2DSkin.defaultSkin).apply {
                        onClick {
                            val json = JSONObject()
                            json.put(DATA_ROOMS_JOIN_ROOMID, d.getString(i.toString()))
                            app.socket.emit(EVENT_ROOMS_JOIN_TO_ROOM, json.toString())
                            app.setScreen<GameScreen>()
                        }
                    })
                    row()
                }
            }
        }
        app.socket.emit(EVENT_ROOMS_GET_LIST)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun hide() {
        view.remove()
    }

}