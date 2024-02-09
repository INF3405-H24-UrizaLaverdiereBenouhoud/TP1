package ca.qc.urizalaverdierebenouhoud.server;
import ca.qc.urizalaverdierebenouhoud.logger.INF3405Logger;

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

    private void handleError(Exception e) {
        ClientHandler.clientHandlerLogger.severe("Error while handling client #" + clientNumber);
        ClientHandler.clientHandlerLogger.severe(e.getMessage());
        closeClientConnection();
    }

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

    private void interpretStreamContent(DataInput in) throws IOException {
        switch (readFirstByte(in)) {
            case 3 -> {
            } //login
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

    private static Byte readFirstByte(DataInput in) {
        try {
            Byte task = in.readByte();
            ClientHandler.clientHandlerLogger.info("Received task: " + task);
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