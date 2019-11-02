package ru.maximus.tictactoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.app.KtxScreen
import ktx.scene2d.defaultStyle
import ktx.scene2d.label
import ktx.scene2d.table
import org.json.JSONArray
import org.json.JSONObject
import ru.maximus.tictactoe.App
import ru.maximus.tictactoe.GAME_COUNT
import ru.maximus.tictactoe.PLAY_GET_GAMES_LIST
import ru.maximus.tictactoe.PLAY_SEND_GAMES_LIST
import java.util.concurrent.ForkJoinPool

class GameListScreen(val stage: Stage, val app: App) : KtxScreen {

    lateinit var roomsTable : Table

    val view = table {
        setFillParent(true)
        top()

        label(text = "Rooms:", style = defaultStyle).cell(row = true)
        roomsTable = table()
    }

    override fun show() {
        stage.addActor(view)
        Gdx.input.inputProcessor = stage

        app.socket!!.on(PLAY_SEND_GAMES_LIST) {
            val o = JSONObject(it[0].toString())
            roomsTable.clear()
            roomsTable = table {
                for (i in 0 until o.getInt(GAME_COUNT)) {
                    label(text = o.getString(i.toString()), style = defaultStyle).cell(row = true)
                }
            }
        }
        app.socket!!.emit(PLAY_GET_GAMES_LIST)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun hide() {
        view.remove()
        app.socket!!.off(PLAY_SEND_GAMES_LIST)
    }

}