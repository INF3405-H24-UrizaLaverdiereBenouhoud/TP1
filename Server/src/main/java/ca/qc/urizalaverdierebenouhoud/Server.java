package ca.qc.urizalaverdierebenouhoud;

import ca.qc.urizalaverdierebenouhoud.logger.INF3405Logger;
import ca.qc.urizalaverdierebenouhoud.message.Message;
import ca.qc.urizalaverdierebenouhoud.server.ClientHandler;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import static ca.qc.urizalaverdierebenouhoud.validate.IPAddress.isValidIpAddress;

public class Server {

    private static final String PROGRAM_USAGE = "Usage: java -jar Server.jar <path/to/accounts.json> <path/to/messages.json'";

    private static final INF3405Logger serverLogger = new INF3405Logger("Server", Server.class.getName());

    private static final String DEFAULT_IP_ADDRESS = "0.0.0.0";
    private static final int DEFAULT_PORT = 5003;

    /**
     * Checks if the given port is valid (between 5000 and 5050)
     * @param port the port to check
     * @return true if the port is valid, false otherwise
     */
    private static boolean isValidPort(int port) {
        return port >= 5000 && port <= 5050;
    }

    /**
     * Prompts the user for an IP address
     * @return the IP address entered by the user
     * @throws IOException if an I/O error occurs
     */
    private static InetAddress promptForIpAddress() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter IP address (default: " + DEFAULT_IP_ADDRESS + ") : ");
        String ipAddress = reader.readLine();
        if (ipAddress.isEmpty()) {
            ipAddress = DEFAULT_IP_ADDRESS;
        }
        if(!isValidIpAddress(ipAddress)) {
            Server.serverLogger.severe("Invalid IP address");
            System.exit(1);
        }
        return InetAddress.getByName(ipAddress);
    }

    /**
     * Prompts the user for a port
     * @return the port entered by the user
     * @throws IOException if an I/O error occurs
     */
    private static int promptForPort() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter port (default: 5003) : ");
        String portString = reader.readLine();
        if (portString.isEmpty()) {
            return DEFAULT_PORT;
        }
        int port = Integer.parseInt(portString);
        if(!isValidPort(port)) {
            Server.serverLogger.severe("Invalid port");
            System.exit(1);
        }
        return port;
    }

    /**
     *  Validates that the provided command line arguments are correct
     *   <br>Usage: java -jar Server.jar <path/to/accounts.json> <path/to/messages.json>
     * @param args the command line arguments
     */
    private static void validateArgs(String[] args) {
        if (args.length != 2) {
            serverLogger.severe(PROGRAM_USAGE);
            System.exit(1);
        }

        for (String arg : args) {
            File file = new File(arg);
            serverLogger.info("Checking file " + file.getAbsolutePath());
            if (file.isDirectory()) {
                serverLogger.severe("File " + file.getAbsolutePath() + " is a directory");
                System.exit(1);
            } else if (!file.exists()) {
                serverLogger.info("File " + file.getAbsolutePath() + " doesn't exist");
                try {
                    if (!file.createNewFile()) {
                        serverLogger.severe("File " + file.getAbsolutePath() + " couldn't be created");
                        System.exit(1);
                    }
                } catch (IOException e) {
                    serverLogger.severe("File " + file.getAbsolutePath() + " couldn't be created: " + e.getMessage());
                    System.exit(1);
                }
                serverLogger.info("File " + file.getAbsolutePath() + " created");
            }
        }
    }

    private static void setupAccountsMessagesFiles(File accountsFile, File messagesFile) {
        System.setProperty("accountsFile", accountsFile.getAbsolutePath());
        Message.setMessagesFile(messagesFile);
    }

    private static void loadMessagesAndAccounts() {
        Message.loadMessages();
//        Message.loadAccounts();
        serverLogger.info("Messages and accounts loaded");
        try {
            Thread.sleep(125);
        } catch (InterruptedException e) {}
    }

    public static void main(String[] args) throws Exception {
        validateArgs(args);
        File accountsFile = new File(args[0]);
        File messagesFile = new File(args[1]);
        setupAccountsMessagesFiles(accountsFile, messagesFile);
        loadMessagesAndAccounts();

        InetAddress serverIP = promptForIpAddress();
        int serverPort = promptForPort();

        ServerSocket server = new ServerSocket(); // initialize server
        //server put on listen
        try (server) {
            startServer(server, serverIP, serverPort);
            int number = 0;
            listenForServerExit(server);
            listenForConnection(server, number);
        } catch (IOException e) {
            serverLogger.severe("Failed to listen for connection: " + e.getMessage());
            System.exit(2);
        }
    }

    /**
     * Listens for a connection and starts a new ClientHandler when a connection is made
     * @param server the server to listen for connections on
     * @param number the number of the client
     * @throws IOException if an I/O error occurs
     */
    private static void listenForConnection(ServerSocket server, int number) throws IOException {
        while (!server.isClosed()) {
            Socket client = server.accept(); // blocks code until connection request is made
            ClientHandler handler = new ClientHandler(client, number++);
            handler.start();
        }
    }

    /**
     *  Listens for the user to type "exit" and closes the server if they do
     * @param server the server to close
     */
    private static void listenForServerExit(ServerSocket server) {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if (line.equals("exit")) {
                    try {
                        server.close();
                    } catch (IOException e) {
                        serverLogger.severe("Failed to close server: " + e.getMessage());
                    }
                    System.exit(0);
                }
            }
        }).start();
    }

    /**
     * Starts the server on the given IP address and port
     * @param server the server to start
     * @param serverIP the IP address to start the server on
     * @param serverPort the port to start the server on
     */
    private static void startServer(ServerSocket server, InetAddress serverIP, int serverPort) {
        try {
           server.setReuseAddress(true); // so socket does not enter timewait state
            server.bind(new InetSocketAddress(serverIP, serverPort)); //define communication endpoint (point d'entré)
            serverLogger.info("Server started on " + serverIP + ":" + serverPort);
        } catch (IOException e) {
            serverLogger.severe("Failed to start server: " + e.getMessage());
            System.exit(2);
        }
    }
}

