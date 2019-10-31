package ru.maximus.tictactoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import io.socket.emitter.Emitter
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.scene2d.*
import org.json.JSONObject
import ru.maximus.tictactoe.App

class AuthScreen(val stage: Stage, val app: App) : KtxScreen {

    lateinit var loginText : TextField
    lateinit var passText : TextField

    val view = table {
        setFillParent(true)

        label(text = "Login: ", style = defaultStyle)
        loginText = textField(text = "", style = defaultStyle).cell(row = true).apply {
            messageText = "user"
        }
        label(text = "Password: ", style = defaultStyle)
        passText = textField(text = "", style = defaultStyle).cell(row = true).apply {
            messageText = "1234"
        }

        textButton(text = "Auth", style = defaultStyle).cell(row = true).apply {
            onClick {
                app.socket!!.once("authSuccess") { args ->
                    if (args[0] as Boolean) {
                        app.setScreen<MainMenuScreen>()
                    }
                }

                val o = JSONObject()
                o.put("login", loginText.text)
                o.put("pass", passText.text)
                app.socket!!.emit("authTry", o.toString())
            }
        }
        textButton(text = "Register", style = defaultStyle).cell(row = true)

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