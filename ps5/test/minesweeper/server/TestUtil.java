package minesweeper.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class TestUtil {
    private static final String LOCALHOST = "localhost";
    private static final int PORT = 4444;
    private static final int MAX_CONNECTION_ATTEMPTS = 10;


    static ThreadWithObituary startServer(boolean debug) {
        return startServer(debug ? "--debug" : "--no-debug");
    }

    static ThreadWithObituary startServer(final String... args) {
        return new ThreadWithObituary(() -> MinesweeperServer.main(args));
    }

    static Socket connect(ThreadWithObituary server) throws IOException {
        int attemps = 0;
        while (true) {
            try {
                Socket socket = new Socket(LOCALHOST, PORT);
                socket.setSoTimeout(3000);
                return socket;
            } catch (ConnectException ce) {
                if (server != null && server.thread().isAlive()) {
                    throw new IOException("Server thread not running.", server.error());
                }
                if (++attemps > MAX_CONNECTION_ATTEMPTS) {
                    throw new IOException("Exceeded max connection attempts", ce);
                }
                try {
                    Thread.sleep(attemps * 10);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }
}

class ThreadWithObituary {
    private final Thread thread;
    private Throwable error = null;

    /**
     * Create and start a new thread.
     */
    ThreadWithObituary(Runnable runnable) {
        thread = new Thread(runnable);
        thread.setUncaughtExceptionHandler((thead, error) -> {
            error.printStackTrace();
        });
        thread.start();
    }

    // return the thread.
    synchronized Thread thread() {
        return thread;
    }

    // return the error that terminated the thread, if any.
    synchronized Throwable error() {
        return error;
    }
}
