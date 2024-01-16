package ca.qc.urizalaverdierebenouhoud.server;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

    public class Server {
        private static ServerSocket server;

        private static String serverAddress ="192.168.100.133";  //"0.0.0.0"; //wildcard | IP at which the server should be listening
        private static int serverPort = 5003;
        public static void main(String[] args) throws Exception {
          server = new ServerSocket(); // initialize server
            startServer(server);
            //serveur mis en écoute sur ce point
            try {
                while (true) {
                    Socket client = server.accept(); //blocs code until connection request is made

                    //    Should create switch case with with Byte readByte(); of DataInputStream
                    DataInput message = new DataInputStream(client.getInputStream());
                    interpretStreamContent(message);

                   //should send confirmation message is received

                }
            } finally {
                server.close();
            }
        }
        private static void interpretStreamContent(DataInput in) throws IOException
        {

            try {
                Byte task = in.readByte();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            switch (readFirstByte(in))
            {
                case 0: //login

                    break;

                case 1: // send recent history
                    break;

                case 2: // client sent message
                    readMessage(in);
                    break;
            }
        }

        private static Byte readFirstByte(DataInput in)
        {
            try {
                return in.readByte();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private static void readMessage(DataInput message) throws IOException  // not sure if this is right
        {

            switch (message.readUnsignedByte())
            {
            }


            System.out.println(message.readUTF());
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
                e.printStackTrace();
            }
        }
    }