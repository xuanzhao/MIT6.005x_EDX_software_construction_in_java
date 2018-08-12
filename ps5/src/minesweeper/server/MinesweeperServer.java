/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper.server;

import minesweeper.Board;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Multiplayer Minesweeper server.
 */
public class MinesweeperServer {

    // System thread safety argument
    //   TODO Problem 5

    /**
     * Default server port.
     */
    private static final int DEFAULT_PORT = 4444;
    /**
     * Maximum port number as defined by ServerSocket.
     */
    private static final int MAXIMUM_PORT = 65535;
    /**
     * Default square board size.
     */
    private static final int DEFAULT_SIZE = 10;

    /**
     * Socket for receiving incoming connections.
     */
    private final ServerSocket serverSocket;
    /**
     * True if the server should *not* disconnect a client after a BOOM message.
     */
    private final boolean debug;

    private static String BOOM_MESSAGE = "BOOM!";
    private static String HELP_MESSAGE = "input <dig y x> <flag y x> <deflag y x> <look> for move on the game, <bye> for quit the game";
    private static String TERMINATE = "terminate";

    private static Board board;
    private AtomicInteger playersNum = new AtomicInteger(0);
    private String helloMessage;

    // TODO: Abstraction function, rep invariant, rep exposure
    // Abstraction function
    //
    // rep invariant

    /**
     * Make a MinesweeperServerSingleThread that listens for connections on port.
     *
     * @param port  port number, requires 0 <= port <= 65535
     * @param debug debug mode flag
     * @throws IOException if an error occurs opening the server socket
     */
    public MinesweeperServer(int port, boolean debug) throws IOException {
        serverSocket = new ServerSocket(port);
        this.debug = debug;
    }

    /**
     * Run the server, listening for client connections and handling them.
     * Never returns unless an exception is thrown.
     *
     * @throws IOException if the main server socket is broken
     *                     (IOExceptions from individual clients do *not* terminate serve())
     */
    public void serve() throws IOException {
        System.out.println("Minesweeper Server is already setup");

        ExecutorService executorService = Executors.newCachedThreadPool();

        try {
            while (true) {
                Socket socket = serverSocket.accept();
                // block until a client connects
                executorService.submit(new ConnectionHandler(socket));
            }
        } finally {
            executorService.shutdown();
        }

    }

    private synchronized void buildHellowMessage() {
        helloMessage = String.format("Welcome to Minesweeper. Players: %d including you. " +
                        "Board: %d rows by %d columns. Type 'help' for help.",
                playersNum.intValue(), board.getBoardYSize(), board.getBoardXSize());
    }

    class ConnectionHandler implements Runnable {
        private Socket socket;

        ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Handle a single client connection. Returns when client disconnects.
         *
         * @param socket socket where the client is connected
         * @throws IOException if the connection encounters an error or terminates unexpectedly
         */
        private void handleConnection(Socket socket) throws IOException {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            playersNum.incrementAndGet();
            buildHellowMessage();
            out.println(helloMessage);

            try {
                for (String line = in.readLine(); line != null; line = in.readLine()) {
                    String output = handleRequest(line);
                    if (output != null) {
                        // TODO: Consider improving spec of handleRequest to avoid use of null
                        if (output.equals(TERMINATE)) {
                            playersNum.decrementAndGet();
                            System.out.printf("player disconnected, current playerer is %d\n", playersNum.intValue());
                            break;
                        } else if (output.equals(BOOM_MESSAGE) && !debug) {
                            out.println(output);
                            playersNum.decrementAndGet();
                            break;
                        } else {
                            out.println(output);
                        }
                    }
                }
            } finally {
                out.close();
                in.close();
            }
        }

        /**
         * Handler for client input, performing requested operations and returning an output message.
         *
         * @param input message from client
         * @return message to client, or null if none
         */
        private String handleRequest(String input) {
            String regex = "(look)|(help)|(bye)|"
                    + "(dig -?\\d+ -?\\d+)|(flag -?\\d+ -?\\d+)|(deflag -?\\d+ -?\\d+)";
            if (!input.matches(regex)) {
                return HELP_MESSAGE;
            }
            String[] tokens = input.split(" ");
            switch (tokens[0]) {
                case "look":
                    return board.showBoardState();
                case "help":
                    return HELP_MESSAGE;
                case "bye":
                    return TERMINATE;
                default:
                    int y = Integer.parseInt(tokens[1]);
                    int x = Integer.parseInt(tokens[2]);
                    switch (tokens[0]) {
                        case "dig":
                            boolean isMine = board.dig(y, x);
                            if (isMine) {
                                return BOOM_MESSAGE;
                            }
                            return board.showBoardState();
                        case "flag":
                            board.flag(y, x);
                            return board.showBoardState();
                        case "deflag":
                            board.deflag(y, x);
                            return board.showBoardState();
                    }
                    break;
            }
            // TODO: Should never get here, make sure to return in each of the cases above
            throw new UnsupportedOperationException();
        }

        @Override
        public void run() {
            try {
                handleConnection(socket);
            } catch (IOException e) {
                e.printStackTrace();// but don't terminate serve()
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Start a MinesweeperServerSingleThread using the given arguments.
     * <p>
     * <br> Usage:
     * MinesweeperServerSingleThread [--debug | --no-debug] [--port PORT] [--size SIZE_X,SIZE_Y | --file FILE]
     * <p>
     * <br> The --debug argument means the server should run in debug mode. The server should disconnect a
     * client after a BOOM message if and only if the --debug flag was NOT given.
     * Using --no-debug is the same as using no flag at all.
     * <br> E.g. "MinesweeperServerSingleThread --debug" starts the server in debug mode.
     * <p>
     * <br> PORT is an optional integer in the range 0 to 65535 inclusive, specifying the port the server
     * should be listening on for incoming connections.
     * <br> E.g. "MinesweeperServerSingleThread --port 1234" starts the server listening on port 1234.
     * <p>
     * <br> SIZE_X and SIZE_Y are optional positive integer arguments, specifying that a random board of size
     * SIZE_X*SIZE_Y should be generated.
     * <br> E.g. "MinesweeperServerSingleThread --size 42,58" starts the server initialized with a random board of size
     * 42*58.
     * <p>
     * <br> FILE is an optional argument specifying a file pathname where a board has been stored. If this
     * argument is given, the stored board should be loaded as the starting board.
     * <br> E.g. "MinesweeperServerSingleThread --file boardfile.txt" starts the server initialized with the board stored
     * in boardfile.txt.
     * <p>
     * <br> The board file format, for use with the "--file" option, is specified by the following grammar:
     * <pre>
     *   FILE ::= BOARD LINE+
     *   BOARD ::= X SPACE Y NEWLINE
     *   LINE ::= (VAL SPACE)* VAL NEWLINE
     *   VAL ::= 0 | 1
     *   X ::= INT
     *   Y ::= INT
     *   SPACE ::= " "
     *   NEWLINE ::= "\n" | "\r" "\n"?
     *   INT ::= [0-9]+
     * </pre>
     * <p>
     * <br> If neither --file nor --size is given, generate a random board of size 10x10.
     * <p>
     * <br> Note that --file and --size may not be specified simultaneously.
     *
     * @param args arguments as described
     */
    public static void main(String[] args) {
        // Command-line argument parsing is provided. Do not change this method.
        boolean debug = false;
        int port = DEFAULT_PORT;
        int sizeX = DEFAULT_SIZE;
        int sizeY = DEFAULT_SIZE;
        Optional<File> file = Optional.empty();

        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        try {
            while (!arguments.isEmpty()) {
                String flag = arguments.remove();
                try {
                    if (flag.equals("--debug")) {
                        debug = true;
                    } else if (flag.equals("--no-debug")) {
                        debug = false;
                    } else if (flag.equals("--port")) {
                        port = Integer.parseInt(arguments.remove());
                        if (port < 0 || port > MAXIMUM_PORT) {
                            throw new IllegalArgumentException("port " + port + " out of range");
                        }
                    } else if (flag.equals("--size")) {
                        String[] sizes = arguments.remove().split(",");
                        sizeX = Integer.parseInt(sizes[0]);
                        sizeY = Integer.parseInt(sizes[1]);
                        file = Optional.empty();
                    } else if (flag.equals("--file")) {
                        sizeX = -1;
                        sizeY = -1;
                        file = Optional.of(new File(arguments.remove()));
                        if (!file.get().isFile()) {
                            throw new IllegalArgumentException("file not found: \"" + file.get() + "\"");
                        }
                    } else {
                        throw new IllegalArgumentException("unknown option: \"" + flag + "\"");
                    }
                } catch (NoSuchElementException nsee) {
                    throw new IllegalArgumentException("missing argument for " + flag);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("unable to parse number for " + flag);
                }
            }
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.err.println("usage: MinesweeperServerSingleThread [--debug | --no-debug] [--port PORT] [--size SIZE_X,SIZE_Y | --file FILE]");
            return;
        }

        try {
            runMinesweeperServer(debug, file, sizeX, sizeY, port);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Start a MinesweeperServerSingleThread running on the specified port, with either a random new board or a
     * board loaded from a file.
     *
     * @param debug The server will disconnect a client after a BOOM message if and only if debug is false.
     * @param file  If file.isPresent(), start with a board loaded from the specified file,
     *              according to the input file format defined in the documentation for main(..).
     * @param sizeX If (!file.isPresent()), start with a random board with width sizeX
     *              (and require sizeX > 0).
     * @param sizeY If (!file.isPresent()), start with a random board with height sizeY
     *              (and require sizeY > 0).
     * @param port  The network port on which the server should listen, requires 0 <= port <= 65535.
     * @throws IOException if a network error occurs
     */
    public static void runMinesweeperServer(boolean debug, Optional<File> file, int sizeX, int sizeY, int port) throws IOException {

        // TODO: Continue implementation here in problem 4
        board = file.map(f -> new Board(f.getAbsolutePath())).orElseGet(() -> new Board(sizeX, sizeY, Optional.empty()));

        MinesweeperServer server = new MinesweeperServer(port, debug);
        server.serve();
    }

}
