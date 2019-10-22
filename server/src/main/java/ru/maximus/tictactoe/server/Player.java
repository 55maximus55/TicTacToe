package ru.maximus.tictactoe.server;

import java.util.UUID;

public class Player {

    UUID id;
    boolean isAuth = false;

    public Player(UUID id) {
        this.id = id;
    }
}
