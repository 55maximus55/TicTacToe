package ru.maximus.tictactoe

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import io.socket.client.IO
import io.socket.client.Socket
import ktx.app.KtxGame
import ktx.assets.toInternalFile
import ktx.async.enableKtxCoroutines
import ktx.inject.Context
import ktx.scene2d.Scene2DSkin
import ktx.style.*
import ru.maximus.tictactoe.screens.*

/** [com.badlogic.gdx.Game] implementation shared by all platforms.  */
class App : KtxGame<Screen>() {

    val context = Context()
    var socket = IO.socket("http://localhost:7777")

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

            bindSingleton(SelectServerScreen(inject(), inject()))
            bindSingleton(MainMenuScreen(inject(), inject()))
            bindSingleton(GamesListScreen(inject(), inject()))
            bindSingleton(SettingsScreen(inject(), inject()))
            bindSingleton(GameScreen(inject(), inject()))
        }

        addScreen(context.inject<SelectServerScreen>())
        addScreen(context.inject<MainMenuScreen>())
        addScreen(context.inject<GamesListScreen>())
        addScreen(context.inject<SettingsScreen>())
        addScreen(context.inject<GameScreen>())

        setScreen<SelectServerScreen>()
    }

    fun createSkin(atlas: TextureAtlas): Skin = skin(atlas) { skin ->
        add(defaultStyle, FreeTypeFontGenerator("Roboto-Regular.ttf".toInternalFile()).generateFont(
                FreeTypeFontGenerator.FreeTypeFontParameter().apply {
                    size = 24
                }
        ))
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
        scrollPane {

        }
        slider {
            knob = skin["knob-v"]
            background = skin["line-h"]
        }
        window {
            titleFont = skin[defaultStyle]
            titleFontColor = Color.BLACK
            background = skin["window-border"]
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        context.inject<Stage>().viewport.update(width, height, true)
    }

    override fun dispose() {}
}