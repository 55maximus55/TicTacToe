package ru.maximus.tictactoe.server;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Server extends Game {

    ServerSocketHints socketHints;
    ServerSocket serverSocket;

    @Override
    public void create() {
        socketHints = new ServerSocketHints();
        socketHints.acceptTimeout = 0;

        serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, 7777, socketHints);
    }

    @Override
    public void render() {
        Socket socket = serverSocket.accept(null);

        // Read data from the socket into a BufferedReader
        BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        try {
            // Read to the next newline (\n) and display that text on labelMessage
            System.out.println(buffer.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
