package ru.maximus.tictactoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.scene2d.defaultStyle
import ktx.scene2d.table
import ktx.scene2d.textButton
import ru.maximus.tictactoe.App

class MainMenuScreen(val stage: Stage, val app: App) : KtxScreen {

    val view = table {
        setFillParent(true)

        textButton(text = "Play", style = defaultStyle).cell(row = true).apply {
            onClick {
                app.setScreen<FindGameScreen>()
            }
        }
        textButton(text = "Friends", style = defaultStyle).cell(row = true)
        textButton(text = "Settings", style = defaultStyle).cell(row = true)
        textButton(text = "Disconnect", style = defaultStyle).cell(row = true).apply {
            onClick {
                app.socket!!.disconnect()
                while (app.socket!!.connected()) {}
                app.setScreen<ConnectToServerScreen>()
            }
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