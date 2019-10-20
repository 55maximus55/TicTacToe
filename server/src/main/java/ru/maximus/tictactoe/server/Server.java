package ru.maximus.tictactoe.server;

import com.badlogic.gdx.Game;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

public class Server extends Game {

    SocketIOServer ioServer;

    @Override
    public void create() {
        Configuration config = new Configuration();
        config.setPort(7777);

        ioServer = new SocketIOServer(config);

        ioServer.addConnectListener(client -> System.out.println("client connected: " + client.getSessionId()));
        ioServer.addDisconnectListener(client -> System.out.println("client connected: " + client.getSessionId()));

        ioServer.start();
    }

    @Override
    public void render() {

    }

    @Override
    public void dispose() {
        super.dispose();
        ioServer.stop();
    }
}
