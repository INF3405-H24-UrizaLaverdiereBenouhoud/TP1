package ca.qc.urizalaverdierebenouhoud;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

    public class Server {
        private static ServerSocket server;

        private static String serverAddress ="0.0.0.0";  //TODO: allow user to change IP at connection
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
            switch ( (int)readFirstByte(in))
            {
                case 0: //login

                    break;

                case 1: // send recent history
                    break;

                case 2: // client sent message
                    System.out.println("task 2 initiated"); //TODO: enlever avant remise
                    // Stays here for debugging pupopose prcq le serveur fonctionne pour 1 personne
                    // mais pas encore avec plusieur clients
                    readMessage(in);
                    break;
                default:
                    System.out.println("No task associated with Byte");
            }
        }

        private static Byte readFirstByte(DataInput in)
        {
            try {
                Byte task = in.readByte();
                System.out.println("task type: "+ task);
                return task;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private static void readMessage(DataInput message) throws IOException  // not sure if this is right
        {
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
                throw new RuntimeException(e);
            }
        }
    }
