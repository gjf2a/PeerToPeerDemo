package edu.hendrix.ferrer.peertopeerdemo;

import android.util.Log;

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
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            sendGreeting(writer);
            String msg = getMessage();
            echoAndClose(writer, msg);
            for (ServerListener listener: listeners) {
                listener.notifyMessage(msg);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void sendGreeting(PrintWriter writer) {
        writer.print(REPLY_HEADER);
    }

    private void echoAndClose(PrintWriter writer, String msg) throws IOException {
        writer.print(msg);
        writer.flush();
        socket.close();
    }

    private String getMessage() throws IOException {
        BufferedReader responses =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder sb = new StringBuilder();
        while (!responses.ready()){}
        while (responses.ready()) {
            sb.append(responses.readLine() + '\n');
        }
        return sb.toString();
    }
}