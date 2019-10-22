package ru.maximus.tictactoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Json
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
        loginText = textField(text = "", style = defaultStyle).cell(row = true)
        label(text = "Password: ", style = defaultStyle)
        passText = textField(text = "", style = defaultStyle).cell(row = true)

        textButton(text = "Auth", style = defaultStyle).cell(row = true).apply {
            onClick {
                val o = JSONObject()
                o.put("login", "a")
                o.put("pass", "b")
                app.socket.emit("auth", o)
            }
        }
        textButton(text = "Register", style = defaultStyle).cell(row = true)
    }

    override fun show() {
        stage.addActor(view)
        Gdx.input.inputProcessor = stage

        createListeners()
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun hide() {
        view.remove()

        removeListeners()
    }

    fun createListeners() {
        app.socket.on("AuthCorrect", Emitter.Listener {

        })
    }

    fun removeListeners() {
        app.socket.listeners("AuthCorrect").clear()
    }

}