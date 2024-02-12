package ca.qc.urizalaverdierebenouhoud.server;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.net.Inet4Address;
import ca.qc.urizalaverdierebenouhoud.users.Account;

public class ClientHandler extends Thread {
    private final  Socket client;
    public static ArrayList<ClientHandler> handlers = new ArrayList<>();
    public boolean isRunning;
    public int clientNumber;

    public ClientHandler(Socket socket, int clientNumber) {
        this.client = socket;
        System.out.println("New connection with client#" + clientNumber + " at" + socket);
    }
  
    public void run() {
        isRunning = true;
        handlers.add(this);
        while (isRunning) {
            try {
                DataInputStream message = new DataInputStream(client.getInputStream());
                interpretStreamContent(message);
            } catch (IOException e) {
                handleError(e);
            }
        }
        System.out.println("disconnected2");
    }

    private void handleError(Exception e) {
        System.out.println(e.getMessage());
        closeClientConnection();
    }

    private void closeClientConnection() {
        try {
            isRunning = false;
            handlers.remove(this);
            client.close();
            System.out.println("client #" + clientNumber + " disconnected");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void interpretStreamContent(DataInput in) throws IOException {
        switch ((int) readFirstByte(in)) {
            case 3 -> {
                try {
                    String[] loginInfo = in.readUTF().split(" : ");
                    Inet4Address ip = (Inet4Address) client.getInetAddress();
                    int port  = client.getPort();
                    Account.login(loginInfo[0], loginInfo[1], ip , port);
                }
                catch (Exception e) {

                }
            }
            case 1 -> {
            } // send recent history
            case 2 -> { // client sent message
                readMessage(in);
            }
            case 4 -> {
                System.out.println("disconnected by will of user");
                closeClientConnection();
            } //stop thread
            default -> {
            }
        }
    }

    private static Byte readFirstByte(DataInput in) {
        try {
            Byte task = in.readByte();
            System.out.println("task type: " + task);
            return task;
        } catch (IOException e) {
            return 0;
        }
    }

    private void readMessage(DataInput message) throws IOException  // not sure if this is right
    {
        String text = message.readUTF();
        if (text.isEmpty())
            return;
        System.out.println(text);
        for (ClientHandler handler : handlers) {
            if (handler.client != this.client) {
                BufferedWriter out = (new BufferedWriter(new OutputStreamWriter(handler.client.getOutputStream())));
                out.write(text);
                out.newLine();
                out.flush();
            }
        }
    }

    private void sendLogginResponse(String message) throws IOException  // not sure if this is right
    {
        for (ClientHandler handler : handlers) {
            if (handler.client != this.client) {
                BufferedWriter out = (new BufferedWriter(new OutputStreamWriter(handler.client.getOutputStream())));
                out.write(message);
                out.newLine();
                out.flush();
            }
        }
    }
}