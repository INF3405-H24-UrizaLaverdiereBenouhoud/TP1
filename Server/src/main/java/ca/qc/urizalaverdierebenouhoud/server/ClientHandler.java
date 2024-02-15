package ca.qc.urizalaverdierebenouhoud.server;

import ca.qc.urizalaverdierebenouhoud.logger.INF3405Logger;
import ca.qc.urizalaverdierebenouhoud.message.Message;
import ca.qc.urizalaverdierebenouhoud.users.Account;
import ca.qc.urizalaverdierebenouhoud.users.Client;

import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends Thread {

    private static final INF3405Logger clientHandlerLogger = new INF3405Logger("ClientHandler", ClientHandler.class.getName());
    private static Socket clientSocket;
    protected static final List<ClientHandler> handlers = new ArrayList<>();
    private boolean isRunning;
    private static int clientNumber;

    public ClientHandler(Socket socket, int clientNumber) {
        clientSocket = socket;
        ClientHandler.clientHandlerLogger.info("New connection #" + clientNumber + " from " + clientSocket.getInetAddress());
    }

    @Override
    public void run() {
        isRunning = true;
        handlers.add(this);
        try {
        DataInputStream message = new DataInputStream(clientSocket.getInputStream());
        while (isRunning) {
                interpretStreamContent(message);
        }
        } catch (IOException e) {
            handleError(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ClientHandler.clientHandlerLogger.info("Disconnected");
    }

    /**
     * Handles an error
     *
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
            clientSocket.close();
            ClientHandler.clientHandlerLogger.info("Client #" + clientNumber + " disconnected from server.");
        } catch (IOException ex) {
            ClientHandler.clientHandlerLogger.severe("Error while closing client connection");
            ClientHandler.clientHandlerLogger.severe(ex.getMessage());
        }
    }

    /**
     * Interprets the content of the input stream
     *
     * @param in the input stream to interpret
     * @throws IOException if an I/O error occurs
     */
    private void interpretStreamContent(DataInput in) throws IOException, InterruptedException {
        switch (readFirstByte(in)) {
            case 3 -> {
                sendLogginResponse(login(in.readUTF()));
            }
            case 1 -> {
                ClientHandler.clientHandlerLogger.info("Client #" + clientNumber + " requested recent history.");
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
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
            default -> {
                ClientHandler.clientHandlerLogger.info("Client #" + clientNumber + " disconnected from server by request.");
                closeClientConnection();
            }
        }
    }

    /**
     * Reads the first byte of the input stream
     *
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
     *
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
            if (handler.clientSocket != this.clientSocket) {
                BufferedWriter out = (new BufferedWriter(new OutputStreamWriter(handler.clientSocket.getOutputStream())));
                out.write(text);
                out.newLine();
                out.flush();
            }
        }
    }

    /**
     * Takes the accounts defined into the provided JSON file and loads them into
     *
     * @param loginInfo The username and password of the account to find
     * @return The Client object corresponding to the account
     */
    public static byte login(String loginInfo) {
        String[] splitLoginInfo = loginInfo.split("]: ");
        String[] userAndPassword = splitLoginInfo[1].split(" : ");
        String username = userAndPassword[0];
        String password = userAndPassword[1];
        Inet4Address clientIp = (Inet4Address) clientSocket.getInetAddress();
        int clientPort = clientSocket.getPort();

        Account clientAccount = new Account(username, password);
        Account.loadAccounts();
        List<Account> accounts = Account.getAccounts();

        Client client;
        for (Account account : accounts) {
            if (account.getUsername().equals(username)) {
                if (account.getPassword().equals(password)) {
                    ClientHandler.clientHandlerLogger.info("existing user is now connected");
                    client = new Client(clientAccount, clientIp, clientPort);
                    return '0';
                } else {
                    ClientHandler.clientHandlerLogger.info("incorrect password");
                    return '2';
                }
            }
        }
        Account.saveAccount(clientAccount);
        client = new Client(clientAccount, clientIp, clientPort);
        ClientHandler.clientHandlerLogger.info("newAccount");
        return '1';
    }

    private void sendLogginResponse(byte task) throws IOException {
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        out.writeByte(task);
        out.flush();
    }

}