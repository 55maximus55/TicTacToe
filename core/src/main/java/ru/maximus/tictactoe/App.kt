package ru.maximus.tictactoe

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.socket.client.IO
import ktx.app.KtxGame
import ktx.async.enableKtxCoroutines
import ktx.inject.Context
import ru.maximus.tictactoe.screens.ConnectToServerScreen
import ktx.scene2d.Scene2DSkin
import ktx.style.*
import ru.maximus.tictactoe.screens.AuthScreen
import ru.maximus.tictactoe.screens.MainMenuScreen

/** [com.badlogic.gdx.Game] implementation shared by all platforms.  */
class App : KtxGame<Screen>() {

    val context = Context()

    var socket = IO.socket("")

    override fun create() {
        enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = 1)
        context.register {
            bindSingleton(TextureAtlas("ui/skin.atlas"))
            bindSingleton<Batch>(SpriteBatch())
            bindSingleton<Viewport>(ScreenViewport())
            bindSingleton(Stage(inject(), inject()))
            bindSingleton(createSkin(inject()))
            bindSingleton(this@App)
            Scene2DSkin.defaultSkin = inject()

            bindSingleton(ConnectToServerScreen(inject(), inject()))
            bindSingleton(AuthScreen(inject(), inject()))
            bindSingleton(MainMenuScreen(inject(), inject()))
        }

        addScreen(context.inject<ConnectToServerScreen>())
        addScreen(context.inject<AuthScreen>())
        addScreen(context.inject<MainMenuScreen>())
    }

    fun createSkin(atlas: TextureAtlas): Skin = skin(atlas) { skin ->
        add(defaultStyle, BitmapFont())
        label {
            font = skin[defaultStyle]
        }
        textField {
            font = skin[defaultStyle]
            fontColor = Color.WHITE
            disabledFontColor = Color.DARK_GRAY
            cursor = skin["dot"]
        }
        textButton {
            font = skin[defaultStyle]
            fontColor = Color.GREEN
            downFontColor = Color.BLUE
        }
    }

    override fun render() {
        super.render()

        if (currentScreen !is ConnectToServerScreen) {
            if (!socket.connected()) {
                setScreen<ConnectToServerScreen>()
            }
        }
    }

    override fun dispose() {
        context.dispose()
    }
}