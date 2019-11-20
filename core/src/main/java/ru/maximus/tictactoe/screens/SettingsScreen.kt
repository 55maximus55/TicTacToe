package ru.maximus.tictactoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.scene2d.*
import ru.maximus.tictactoe.App

class SettingsScreen(val stage: Stage, val app: App) : KtxScreen {

    val view = table {
        setFillParent(true)

        label(text = "Settings", style = defaultStyle).cell(row = true)

        label(text = "Volume")
        slider(min = 0f, max = 1f, step = 0.01f, style = defaultStyle).cell(row = true)

        textButton(text = "Back", style = defaultStyle).cell(row = true).apply {
            onClick {
                app.setScreen<MainMenuScreen>()
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