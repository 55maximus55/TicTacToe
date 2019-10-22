package ru.maximus.tictactoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.scene2d.*
import ru.maximus.tictactoe.App

class ConnectToServerScreen(val stage: Stage, val app: App) : KtxScreen {

    lateinit var addressInput : TextField
    lateinit var connectInfoLabel : Label

    val view = table {
        setFillParent(true)

        label(text = "Server address: ", style = defaultStyle)
        addressInput = textField(text = "localhost:7777", style = defaultStyle).apply {
            messageText = "address"
        }
        row()
        textButton("Connect", style = defaultStyle).apply {
            onClick {
                app.setScreen<AuthScreen>()
            }
        }
        connectInfoLabel = label(text = "Connecting", style = defaultStyle).apply {
            color = Color.GRAY
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