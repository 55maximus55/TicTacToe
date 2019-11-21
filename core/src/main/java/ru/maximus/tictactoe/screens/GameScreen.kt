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
                    app.socket.emit(EVENT_ROOMS_LEAVE)
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

        app.socket.once(EVENT_ROOMS_KICK) { args ->
            app.setScreen<MainMenuScreen>()
        }
        app.socket.on(EVENT_ROOMS_PLAYER_JOINED) { data ->
            val json = JSONObject(data[0].toString())
            playersTable.apply {
                add(Label(json.getString(DATA_ROOMS_JOINED_PLAYER_NAME), Scene2DSkin.defaultSkin, defaultStyle))
                row()
            }
        }
        app.socket.once(EVENT_ROOMS_GET_PLAYER_LIST) { data ->
            val d = JSONObject(data[0].toString())
            val n = d.getInt(DATA_ROOMS_PLAYERS_GET_LIST_COUNT)
            println(d)
            playersTable.apply {
                clear()
                for (i in 0 until n) {
                    add(Label(d.getString(i.toString()), Scene2DSkin.defaultSkin, defaultStyle))
                    row()
                }
            }
        }

        app.socket.emit(EVENT_ROOMS_GET_PLAYER_LIST)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun hide() {
        view.remove()
    }

}