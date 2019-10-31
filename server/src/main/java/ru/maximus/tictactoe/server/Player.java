package ru.maximus.tictactoe.server;

import java.util.UUID;

public class Player {

    UUID sessionId;
    int dbId = -1;

    public Player(UUID id) {
        this.sessionId = id;
    }
}
