package minesweeper.server;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.Buffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HelloMessageTest {

    @Test(timeout = 10000)
    public void testHellowMessage() throws IOException, InterruptedException {
        final int THREADS = 2;

        final ThreadWithObituary serverThread = TestUtil.startServer(true);

        Thread threads[] = new Thread[THREADS];
        final CountDownLatch latch = new CountDownLatch(THREADS);

        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {
                try {
                    Socket socket = TestUtil.connect(serverThread);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String moo = in.readLine();
                    System.out.println("moo is " + moo);
                    assertTrue(moo != null);  // to prove socket is connected
                    latch.countDown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        // starting 2 threads for handle client socket connection.
        for (int i = 0; i < THREADS; i++) {
            threads[i].start();
        }
        latch.await(2, TimeUnit.SECONDS);

        // now, we will build third socket.
        final String expected = String.format("Welcome to Minesweeper. Players: %d including you. Board: 10 rows by 10 columns. Type 'help' for help.", THREADS + 1);
        Socket socket = TestUtil.connect(serverThread);
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String moo = in.readLine();
            System.out.println("moo is " + moo);
            assertEquals(expected, moo);
        } catch (SocketTimeoutException ste) {
            fail("server timeout");
        } finally {
            socket.close();
        }

    }
}
