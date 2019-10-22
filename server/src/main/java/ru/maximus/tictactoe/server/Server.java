package ru.maximus.tictactoe.server;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.Json;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.VoidAckCallback;
import com.corundumstudio.socketio.listener.DataListener;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.UUID;

public class Server extends Game {

    SocketIOServer ioServer;

    HashMap<UUID, Player> players = new HashMap<>();
    HashMap<UUID, Room> rooms = new HashMap<>();

    @Override
    public void create() {
        Configuration config = new Configuration();
        config.setPort(7777);

        ioServer = new SocketIOServer(config);

        ioServer.addConnectListener(client -> {
            System.out.println("client connected: " + client.getSessionId());
            players.put(client.getSessionId(), new Player(client.getSessionId()));
        });
        ioServer.addDisconnectListener(client -> {
            System.out.println("client disconnected: " + client.getSessionId());
            players.remove(client.getSessionId());
        });

        ioServer.addEventListener("auth", JSONObject.class, (client, data, ackSender) -> {
            System.out.println(data);
        });
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
