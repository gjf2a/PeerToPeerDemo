package edu.hendrix.ferrer.peertopeerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {

    private EditText outgoing, incoming, host;
    private TextView myIpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myIpView = findViewById(R.id.myIpView);
        outgoing = findViewById(R.id.outgoing);
        incoming = findViewById(R.id.incoming);
        host = findViewById(R.id.host);

        try {
            myIpView.setText(Utilities.getLocalIpAddress());
        } catch (SocketException e) {
            Log.e("MainActivity", "Threw exception when finding ip address");
        }

        final Button send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(outgoing.getText().toString(), host.getText().toString(), Server.APP_PORT);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Server s = new Server();
                    s.addListener(new ServerListener() {
                        @Override
                        public void notifyMessage(String msg) {
                            showIncoming(msg);
                        }
                    });
                    s.listen();
                } catch (IOException e) {
                    Log.e(MainActivity.class.getName(), "Could not start server");
                }
            }
        }).start();


    }

    private void showIncoming(final String msg) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                incoming.setText(msg);
            }
        });
    }

    public void send(final String message, final String host, final int port) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Socket target = new Socket(host, port);
                    sendOver(target, message);
                    showIncoming(receive(target));
                    target.close();
                } catch (final Exception e) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utilities.notifyException(MainActivity.this, e);
                        }
                    });
                }

            }
        }.start();
    }

    public static void sendOver(Socket target, String message) throws IOException {
        PrintWriter sockout = new PrintWriter(target.getOutputStream());
        sockout.println(message);
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
