package ca.qc.urizalaverdierebenouhoud.server;

import ca.qc.urizalaverdierebenouhoud.logger.INF3405Logger;
import ca.qc.urizalaverdierebenouhoud.message.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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
            } //login
            case 1 -> {
                ClientHandler.clientHandlerLogger.info("Client #" + clientNumber + " requested recent history.");
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                for (Message message : Message.getUpToLast15Messages()) {
                    clientHandlerLogger.info("Sending message (history): " + message.toString());
                    out.write(message.toString());
                    out.newLine();
                    out.flush();
                }
                clientHandlerLogger.info("Sent recent history to client #" + clientNumber);
                out.write("historic-end");
                out.newLine();
                out.flush();
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
        Message.saveMessage(Message.parseMessageFromString(text));
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
}