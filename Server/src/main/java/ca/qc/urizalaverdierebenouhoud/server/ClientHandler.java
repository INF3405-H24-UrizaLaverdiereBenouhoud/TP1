package ca.qc.urizalaverdierebenouhoud.server;

import ca.qc.urizalaverdierebenouhoud.logger.INF3405Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.net.Inet4Address;
import ca.qc.urizalaverdierebenouhoud.users.Account;
import java.util.List;

public class ClientHandler extends Thread {

    private static final INF3405Logger clientHandlerLogger = new INF3405Logger("ClientHandler", null);
    private final  Socket client;
    protected static final List<ClientHandler> handlers = new ArrayList<>();
    private boolean isRunning;
    private int clientNumber;

    public ClientHandler(Socket socket, int clientNumber) {
        this.client = socket;
        ClientHandler.clientHandlerLogger.info("New connection #" + clientNumber + " from " + client.getInetAddress());
    }

    @Override
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
        ClientHandler.clientHandlerLogger.info("Disconnected");
    }

    /**
     * Handles an error
     * @param e the error to handle
     */
    private void handleError(Exception e) {
        ClientHandler.clientHandlerLogger.severe("Error while handling client #" + clientNumber);
        ClientHandler.clientHandlerLogger.severe(e.getMessage());
        closeClientConnection();
    }

    /**
     * Closes the client connection
     */
    private void closeClientConnection() {
        try {
            isRunning = false;
            handlers.remove(this);
            client.close();
            ClientHandler.clientHandlerLogger.info("Client #" + clientNumber + " disconnected from server.");
        } catch (IOException ex) {
            ClientHandler.clientHandlerLogger.severe("Error while closing client connection");
            ClientHandler.clientHandlerLogger.severe(ex.getMessage());
        }
    }

    /**
     * Interprets the content of the input stream
     * @param in the input stream to interpret
     * @throws IOException if an I/O error occurs
     */
    private void interpretStreamContent(DataInput in) throws IOException {
        switch (readFirstByte(in)) {
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
                ClientHandler.clientHandlerLogger.info("Client #" + clientNumber + " disconnected from server by request.");
                closeClientConnection();
            }
        }
    }

    /**
     * Reads the first byte of the input stream
     * @param in the input stream to read from
     * @return the first byte of the input stream
     */
    private static Byte readFirstByte(DataInput in) {
        try {
            Byte task = in.readByte();
            ClientHandler.clientHandlerLogger.info("Received task: " + task);
            return task;
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * Reads a message from the client and sends it to all other clients
     * @param message the message to read
     * @throws IOException if an I/O error occurs
     */
    private void readMessage(DataInput message) throws IOException  // not sure if this is right
    {
        String text = message.readUTF();
        if (text.isEmpty())
            return;
        ClientHandler.clientHandlerLogger.info("(" + clientNumber + ")" + text);
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