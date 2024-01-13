package ca.qc.urizalaverdierebenouhoud;


import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
public class Server {

    private static ServerSocket Listener;
    private static DataInputStream in;
    private static Socket socket = null;

    public static void main(String[] args) throws Exception
    {
        int clientNumber = 0;
        String serverAddress = "10.200.14.166";
        int serverPort = 5000;
        Listener = new ServerSocket();
        Listener.setReuseAddress(true);
        InetAddress serverIP = InetAddress.getByName(serverAddress);
        Listener.bind(new InetSocketAddress(serverIP,serverPort));
        System.out.format("The server is running on %s:%d%n", serverAddress, serverPort);
        try {
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
            String line = "";
            while (true) {
                new ClientHandler(Listener.accept(), clientNumber++).start();
                while(!line.equals("over"))
                {

                    try {line = in.readUTF();
                        System.out.println(line);
                    }
                    catch(IOException i)
                    {
                        System.out.println(i);
                    }

                }
            }}
        finally {
            // Fermeture de la connexion
            Listener.close();
        } } }




public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}