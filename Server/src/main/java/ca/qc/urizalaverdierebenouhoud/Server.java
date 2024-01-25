package ca.qc.urizalaverdierebenouhoud;

import ca.qc.urizalaverdierebenouhoud.server.ClientHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(); // initialize server
        //server put on listen
        try (server) {
            startServer(server);
            int number = 0;
            while (true) {
                Socket client = server.accept(); //blocs code until connection request is made
                ClientHandler handler = new ClientHandler(client, number++);
                handler.start();
                //should send confirmation message is received
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startServer(ServerSocket server) {
        try {
            server.setReuseAddress(true); // so socket does not enter timewait state
            //TODO: allow user to change IP at connection
            String serverAddress = "0.0.0.0";
            int serverPort = 5003;
            InetAddress serverIP = InetAddress.getByName(serverAddress);
            server.bind(new InetSocketAddress(serverIP, serverPort)); //define communication endpoint (point d'entré)
            System.out.format("The server is running on %s:%d %n", serverAddress, serverPort);
        } catch (IOException e) {
            //throw new RuntimeException(e);
        }
    }
}
