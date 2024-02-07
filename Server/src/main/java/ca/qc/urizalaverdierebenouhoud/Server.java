package ca.qc.urizalaverdierebenouhoud;
import ca.qc.urizalaverdierebenouhoud.server.ClientHandler;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final String DEFAULT_IP_ADDRESS = "0.0.0.0";
    private static final int DEFAULT_PORT = 5003;

    /**
     *  Checks if the given string is a valid IP address
     * @param ipAddress the string to check
     * @return true if the string is a valid IP address, false otherwise
     */
    private static boolean isValidIpAddress(String ipAddress) {
        String[] tokens = ipAddress.split("\\.");
        if (tokens.length != 4) {
            return false;
        }
        for (String token : tokens) {
            int tokenInt = Integer.parseInt(token);
            if (tokenInt < 0 || tokenInt > 255) {
                return false;
            }
        }
        return true;
    }

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
            System.err.println("Invalid IP address");
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
            System.err.println("Invalid port");
            System.exit(1);
        }
        return port;
    }

    public static void main(String[] args) throws Exception {
        InetAddress serverIP = promptForIpAddress();
        int serverPort = promptForPort();

        ServerSocket server = new ServerSocket(); // initialize server
        //server put on listen
        try (server) {
            startServer(server, serverIP, serverPort);
            int number = 0;
            while (!server.isClosed()) {
                Socket client = server.accept(); //blocs code until connection request is made
                ClientHandler handler = new ClientHandler(client, number++);
                handler.start();
                //should send confirmation message is received
            }
        } catch (IOException e) {
            System.out.println("Server rip");
            throw new RuntimeException(e);
        }
    }

    private static void startServer(ServerSocket server, InetAddress serverIP, int serverPort) {
        try {
           server.setReuseAddress(true); // so socket does not enter timewait state

            server.bind(new InetSocketAddress(serverIP, serverPort)); //define communication endpoint (point d'entré)
            System.out.format("The server is running on %s:%d %n", serverIP, serverPort);
        } catch (IOException e) {
            //throw new RuntimeException(e);
        }
    }
}

