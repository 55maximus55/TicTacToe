package ru.maximus.tictactoe.server

import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration

/** Launches the server application.  */
fun main(args: Array<String>) {
    val config = HeadlessApplicationConfiguration()

    HeadlessApplication(Server())
}