package ru.maximus.tictactoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import io.socket.client.IO
import io.socket.client.Socket
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.scene2d.*
import ru.maximus.tictactoe.App

class SelectServerScreen(val stage: Stage, val app: App) : KtxScreen {

    val view = table {
        setFillParent(true)

        val prefs = Gdx.app.getPreferences("tictactoe")

        label(text = "Server address", style = defaultStyle).cell(row = true)
        val address = textField(prefs.getString("last_address", "localhost"), style = defaultStyle).cell(row = true).apply {
            messageText = "kek"
        }
        textButton(text = "Connect", style = defaultStyle).cell(row = true).apply {
            onClick {
                prefs.putString("last_address", address.text)
                prefs.flush()

                app.socket.close()
                app.socket = IO.socket("http://${address.text}").apply {
                    on(Socket.EVENT_CONNECT) {
                        app.setScreen<MainMenuScreen>()
                    }
                    on(Socket.EVENT_DISCONNECT) {
                        close()
                        app.setScreen<SelectServerScreen>()
                    }
                    connect()
                }
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