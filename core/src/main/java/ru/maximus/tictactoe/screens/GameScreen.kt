package ru.maximus.tictactoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.scene2d.defaultStyle
import ktx.scene2d.label
import ktx.scene2d.table
import ktx.scene2d.textButton
import ru.maximus.tictactoe.App
import ru.maximus.tictactoe.ROOMS_KICK
import ru.maximus.tictactoe.ROOMS_LEAVE
import ru.maximus.tictactoe.ROOMS_PLAYER_LEFT

class GameScreen(val stage: Stage, val app: App) : KtxScreen {

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
                    app.socket.emit(ROOMS_LEAVE)
                }
            }
            label(text = "Player1: ").cell(row = true)
            label(text = "Player2: ").cell(row = true)
        }
    }

    override fun show() {
        stage.addActor(view)
        Gdx.input.inputProcessor = stage

        app.socket.once(ROOMS_KICK) { args ->
            app.setScreen<MainMenuScreen>()
        }
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun hide() {
        view.remove()
    }

}