package edu.hendrix.ferrer.peertopeerdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by gabriel on 2/15/19.
 */

public class SocketEchoThread extends Thread {
    private Socket socket;
    private ArrayList<ServerListener> listeners = new ArrayList<>();

    public static final String REPLY_HEADER = "Connection open.\nI will echo a single message, then close.\n";

    public SocketEchoThread(Socket socket, ArrayList<ServerListener> listeners) {
        this.socket = socket;
        this.listeners.addAll(listeners);
    }

    public void run() {
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            String msg = Communication.receive(socket);
            Communication.sendOver(socket, msg);
            socket.close();
            for (ServerListener listener: listeners) {
                listener.notifyMessage(msg);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}