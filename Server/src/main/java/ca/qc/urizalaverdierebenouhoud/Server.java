package ca.qc.urizalaverdierebenouhoud;

import ca.qc.urizalaverdierebenouhoud.server.ClientHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
        private static ServerSocket server;

        private static String serverAddress ="10.200.14.166";  //TODO: allow user to change IP at connection
        private static int serverPort = 5003;
        public static void main(String[] args) throws Exception {
          server = new ServerSocket(); // initialize server
            startServer(server);
            //serveur mis en écoute sur ce point
            try {
                int number = 0;
                while (true) {
                    Socket client = server.accept(); //blocs code until connection request is made
                  ClientHandler handler = new ClientHandler(client,number++);
                  handler.start();
                   //should send confirmation message is received
                }
            }
            catch (IOException e){}
            finally {
                server.close();
            }
        }
        private static void startServer(ServerSocket server)
        {
             try{
                 server.setReuseAddress(true); // so socket does not enter timewait state
                 InetAddress serverIP = InetAddress.getByName(serverAddress);
                 server.bind(new InetSocketAddress(serverIP, serverPort)); //define communication endpoint (point d'entré)
                 System.out.format("The server is running on %s:%d %n", serverAddress, serverPort);
             }
            catch (IOException e){
                //throw new RuntimeException(e);
            }
        }
    }
