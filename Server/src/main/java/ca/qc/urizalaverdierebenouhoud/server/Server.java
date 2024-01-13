package ca.qc.urizalaverdierebenouhoud.server;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

    public class Server {
        private static ServerSocket Listener;

        public static void main(String[] args) throws Exception {

            int clientNumber = 0;
            String serverAddress =  "10.0.0.107"; //needs to change depending on location | should also make separate file for such variables
            int serverPort = 5003;
            Listener = new ServerSocket();
            Listener.setReuseAddress(true);
            InetAddress serverIP = InetAddress.getByName(serverAddress);
            Listener.bind(new InetSocketAddress(serverIP, serverPort));
            System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
            try {
                while (true) {
                    new ClientHandler(Listener.accept(), clientNumber++).start();
                }
            } finally {
                Listener.close();
            }
        }
    }