package edu.hendrix.ferrer.peertopeerdemo;

import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void test_send_receive() throws Exception {
        final String testMsg = "This is a test.\nThis is only a test.\n";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Server s = new Server();
                    s.listenOnce().start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Socket test = new Socket("localhost", Server.APP_PORT);
        Communication.sendOver(test, testMsg);
        String result = Communication.receive(test);
        assertEquals(testMsg, result);
    }
}