@file:JvmName("DesktopLauncher")

package ru.maximus.tictactoe.desktop

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.Files.FileType
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import ru.maximus.tictactoe.App

/** Launches the desktop (LWJGL) application.  */
fun main(args: Array<String>) {
    val cfg = LwjglApplicationConfiguration().apply {
        title = "TIC TAC TOE"

        width = 800
        height = 600
        resizable = true

        intArrayOf(128, 64, 32, 16).forEach {
            addIcon("libgdx$it.png", FileType.Internal)
        }
    }
    LwjglApplication(App() as ApplicationListener, cfg)
}