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
import ru.maximus.tictactoe.PLAY_FIND_GAME

class FindGameScreen(val stage: Stage, val app: App) : KtxScreen {

    val view = table {
        setFillParent(true)

        label(text = "Searching for a player", style = defaultStyle).cell(row = true)
    }

    override fun show() {
        stage.addActor(view)
        Gdx.input.inputProcessor = stage

        app.socket!!.emit(PLAY_FIND_GAME)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun hide() {
        view.remove()
    }

}