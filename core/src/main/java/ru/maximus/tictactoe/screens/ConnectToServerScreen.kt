package ru.maximus.tictactoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import io.socket.client.IO
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.scene2d.*
import ru.maximus.tictactoe.App
import java.lang.Exception

class ConnectToServerScreen(val stage: Stage, val app: App) : KtxScreen {

    val connectWaitTime = 5f
    var connectionTimer = connectWaitTime

    lateinit var addressInput : TextField
    lateinit var connectInfoLabel : Label

    val view = table {
        setFillParent(true)

        label(text = "Server address: ", style = defaultStyle)
        addressInput = textField(text = "", style = defaultStyle).apply {
            messageText = "address"
        }
        row()
        textButton("Connect", style = defaultStyle).apply {
            onClick {
                app.socket.disconnect()
                try {
                    app.socket = IO.socket("http://${addressInput.text}")
                    app.socket.connect()
                    connectionTimer = 0f
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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

        connectionTimer += delta
        if (connectionTimer <= connectWaitTime) {
            connectInfoLabel.setText("Connecting")
            addressInput.isDisabled = true
        }
        else {
            connectInfoLabel.setText("")
            addressInput.isDisabled = false
        }

        if (app.socket.connected()) {
            app.setScreen<AuthScreen>()
            connectionTimer = connectWaitTime
        }
    }

    override fun hide() {
        view.remove()
    }

}