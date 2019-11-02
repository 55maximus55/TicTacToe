package ru.maximus.tictactoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxScreen
import ktx.scene2d.defaultStyle
import ktx.scene2d.table
import ktx.scene2d.textButton
import ru.maximus.tictactoe.App
import ru.maximus.tictactoe.PLAY_CREATE_GAME
import ru.maximus.tictactoe.PLAY_CREATE_GAME_SUCCESS

class NewGameScreen(val stage: Stage, val app: App) : KtxScreen {

    val view = table {
        setFillParent(true)

        textButton(text = "Create room", style = defaultStyle).cell(row = true).apply {
            if (app.socket != null) {
                app.socket!!.once(PLAY_CREATE_GAME_SUCCESS) {
                    if (it[0] as Boolean) {
                        println(it[0])
                        app.setScreen<GameScreen>()
                    }
                }

                app.socket!!.emit(PLAY_CREATE_GAME)
            }
        }
        textButton(text = "Find room", style = defaultStyle).cell(row = true).apply {

        }
    }

    override fun show() {
        stage.addActor(view)
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun hide() {
        view.remove()
    }

}