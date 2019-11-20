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

class MainMenuScreen(val stage: Stage, val app: App) : KtxScreen {

    val view = table {
        setFillParent(true)

        label(text = "Main Menu", style = defaultStyle).cell(row = true)

        textButton(text = "Play", style = defaultStyle).cell(row = true).apply {
            onClick {
                app.setScreen<GamesListScreen>()
            }
        }
        textButton(text = "Settings", style = defaultStyle).cell(row = true).apply {
            onClick {
                app.setScreen<SettingsScreen>()
            }
        }
        textButton(text = "Exit", style = defaultStyle).cell(row = true).apply {
            onClick {
                Gdx.app.exit()
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