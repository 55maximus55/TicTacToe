package ru.maximus.tictactoe.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.scene2d.*
import ru.maximus.tictactoe.App

class GamesListScreen(val stage: Stage, val app: App) : KtxScreen {

    val view = table {
        setFillParent(true)

        table {
            table {
                textButton(text = "Back", style = defaultStyle).cell().apply {
                    onClick {
                        app.setScreen<MainMenuScreen>()
                    }
                }
                label(text = "Games", style = defaultStyle).cell(row = true)
            }.cell(row = true)
            scrollPane(style = defaultStyle) {
                table {
                    for (i in 1 .. 50) {
                        textButton(text = "i: $i", style = defaultStyle).cell(row = true).apply {
                            onClick {
                                app.setScreen<GameScreen>()
                            }
                        }
                    }
                }
            }
        }.cell()
        table {
            textButton(text = "Create", style = defaultStyle).cell(row = true).apply {
                onClick {
                    app.setScreen<GameScreen>()
                }
            }
        }.cell()
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