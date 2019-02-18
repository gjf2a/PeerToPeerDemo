package edu.hendrix.ferrer.peertopeerdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by gabriel on 2/18/19.
 */

public class Communication {
    public static void sendOver(Socket target, String message) throws IOException {
        PrintWriter sockout = new PrintWriter(target.getOutputStream());
        sockout.print(message);
        sockout.flush();
    }

    public static String receive(Socket target) throws IOException {
        BufferedReader sockin = new BufferedReader(new InputStreamReader(target.getInputStream()));
        while (!sockin.ready()) {}
        StringBuilder input = new StringBuilder();
        while (sockin.ready()) {
            input.append(sockin.readLine());
            input.append("\n");
        }
        return input.toString();
    }
}
