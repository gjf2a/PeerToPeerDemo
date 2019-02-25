package edu.hendrix.ferrer.peertopeerdemo;

/**
 * Created by gabriel on 2/15/19.
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static final int APP_PORT = 8888;

    private static Server instance;

    public static Server get() throws IOException {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    private ServerSocket accepter;
    private ArrayList<ServerListener> listeners = new ArrayList<>();

    private Server() throws IOException {
        accepter = new ServerSocket(APP_PORT);
    }

    public void addListener(ServerListener listener) {
        this.listeners.add(listener);
    }

    public void listen() throws IOException {
        for (;;) {
            listenOnce().start();
        }
    }

    public SocketEchoThread listenOnce() throws IOException {
        Socket s = accepter.accept();
        SocketEchoThread echoer = new SocketEchoThread(s, listeners);
        return echoer;
    }
}
