package ca.qc.urizalaverdierebenouhoud.server;

import ca.qc.urizalaverdierebenouhoud.users.Account;
import ca.qc.urizalaverdierebenouhoud.users.InvalidUsernamePasswordComboException;
import ca.qc.urizalaverdierebenouhoud.users.MainClient;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;
import java.net.Inet4Address;


public class ClientHandler extends Thread {
    private final Socket client;

    public ClientHandler(Socket socket, int clientNumber) {
        this.client = socket;
        System.out.println("New connection with client#" + clientNumber + " at" + socket);
    }

    public void run() {
        while (client.isConnected()) {
            try {
                Thread.sleep(500);
                DataInputStream message = new DataInputStream(client.getInputStream());
                interpretStreamContent(message);
                if (!client.isConnected())
                    client.close();
            } catch (IOException | InterruptedException | InvalidUsernamePasswordComboException e) {
                System.out.println("client disconected");
            }
        }
    }

    private static void interpretStreamContent(DataInput in) throws IOException, InvalidUsernamePasswordComboException {
        switch ((int) readFirstByte(in)) {
            case 3 -> {
                String[] loginInfo = in.readUTF().split(", ");
                InetAddress ipAddress = Inet4Address.getByName(loginInfo[3]);
                Inet4Address ip = (Inet4Address) ipAddress;
                Account.login(loginInfo[0], loginInfo[1], ip , Integer.parseInt(loginInfo[4]));
            } //login
            case 1 -> {
            } // send recent history
            case 2 -> { // client sent message
                System.out.println("task 2 initiated"); //TODO: enlever avant remise
                // Stays here for debugging pupopose prcq le serveur fonctionne pour 1 personne
                // mais pas encore avec plusieur clients
                readMessage(in);
            }
            default -> {
            }
            //System.out.println("No task associated with Byte or user disconnected");
        }
    }

    private static Byte readFirstByte(DataInput in) {
        try {
            Byte task = in.readByte();
            System.out.println("task type: " + task);
            return task;
        } catch (IOException e) {
            //throw new RuntimeException(e);
            return 0;
        }
    }

    private static void readMessage(DataInput message) throws IOException  // not sure if this is right
    {
        System.out.println(message.readUTF());
    }
}