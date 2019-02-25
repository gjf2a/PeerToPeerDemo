package edu.hendrix.ferrer.peertopeerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {

    private EditText outgoing, incoming, host;
    private TextView myIpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupComponents();
        setupClient();
        setupServer();
    }

    private void setupComponents() {
        myIpView = findViewById(R.id.myIpView);
        outgoing = findViewById(R.id.outgoing);
        incoming = findViewById(R.id.incoming);
        host = findViewById(R.id.host);

        try {
            myIpView.setText(Utilities.getLocalIpAddress());
        } catch (SocketException e) {
            Log.e("MainActivity", "Threw exception when finding ip address");
        }
    }

    private void setupClient() {
        Button send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(outgoing.getText().toString(), host.getText().toString(), Server.APP_PORT);
            }
        });
    }

    private void setupServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Server.get().addListener(new ServerListener() {
                        @Override
                        public void notifyMessage(String msg) {
                            showIncoming(msg);
                        }
                    });
                    Server.get().listen();
                } catch (IOException e) {
                    Log.e(MainActivity.class.getName(), "Could not start server");
                }
            }
        }).start();
    }

    private void showIncoming(final String msg) {
        runOnUiThread(new Runnable() {
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
                    Communication.sendOver(target, message);
                    showIncoming(Communication.receive(target));
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

}
