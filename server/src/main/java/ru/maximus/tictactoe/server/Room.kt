package ru.maximus.tictactoe.server

import ru.maximus.tictactoe.DATA_CELL_EMPTY
import java.util.*
import kotlin.collections.ArrayList

class Room {

    val players = ArrayList<UUID>()
    val maxPlayer = 2

    val cells = ArrayList<Int>().apply {
        for (i in 0 until 9) {
            add(DATA_CELL_EMPTY)
        }
    }
    var isCross = true
    var firstPlayerCross = true

}