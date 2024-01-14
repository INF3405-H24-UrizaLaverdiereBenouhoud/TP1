package ca.qc.urizalaverdierebenouhoud.server;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

    public class Server {
        private static ServerSocket server;

        public static void main(String[] args) throws Exception {


          server = new ServerSocket(); // initialize server
            String serverAddress =  "0.0.0.0"; //wildcard | IP at which the server should be listening
            int serverPort = 5003;
            startServer(server,serverAddress,serverPort);
            //serveur mis en écoute sur ce point
            try {
                while (true) {
                    Socket client = server.accept(); //blocs code until connection request is made
                    BufferedInputStream message = new BufferedInputStream(client.getInputStream());
                   readMessage(message);
                }
            } finally {
                server.close();
            }
        }
        private static void readMessage(BufferedInputStream message) throws IOException {
            int letter;
            if(message ==null)
                return;
            while ((letter=message.read()) != -1)
                System.out.print((char)letter);
        }

        private static void startServer(ServerSocket server, String serverAddress, int serverPort)
        {
             try{
                 server.setReuseAddress(true); // so socket does not enter timewait state
                 InetAddress serverIP = InetAddress.getByName(serverAddress);
                 server.bind(new InetSocketAddress(serverIP, serverPort)); //define communication endpoint (point d'entré)
                 System.out.format("The server is running on %s:%d %n", serverAddress, serverPort);
             }
            catch (IOException e){
                e.printStackTrace();
            }

        }

    }