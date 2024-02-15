package ca.qc.urizalaverdierebenouhoud;

import ca.qc.urizalaverdierebenouhoud.logger.INF3405Logger;
import ca.qc.urizalaverdierebenouhoud.message.Message;
import ca.qc.urizalaverdierebenouhoud.users.Account;
import ca.qc.urizalaverdierebenouhoud.users.Client;
import ca.qc.urizalaverdierebenouhoud.validate.IPAddress;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

public class MainClient {

    private static final INF3405Logger mainClientLogger = new INF3405Logger("MainClient", MainClient.class.getName());

    private static final String DEFAULT_IP_ADDRESS = "0.0.0.0";
    private static final int DEFAULT_PORT = 5003;
    public static final int MAX_MESSAGE_LENGTH = 200;
    public static final int EXIT_CODE_MESSAGE = 4;
    private static boolean isRunning = true;
    private static InetAddress serverIpAddress;
    private static int serverPort;

    /**
     * Checks if the given port is valid (between 5000 and 5050)
     *
     * @param port the port to check
     * @return true if the port is valid, false otherwise
     */
    private static boolean isValidPort(int port) {
        return port >= 5000 && port <= 5050;
    }

    /**
     * Prompts the user for an IP address
     *
     * @return the IP address entered by the user
     * @throws IOException if an I/O error occurs
     */
    private static InetAddress promptForIpAddress() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter IP address (default: " + DEFAULT_IP_ADDRESS + ") : ");
        String ipAddress = reader.readLine();
        if (ipAddress.isEmpty()) {
            ipAddress = DEFAULT_IP_ADDRESS;
        }
        if (!IPAddress.isValidIpAddress(ipAddress)) {
            MainClient.mainClientLogger.severe("Invalid IP address");
            System.exit(1);
        }
        return InetAddress.getByName(ipAddress);
    }

    /**
     * Prompts the user for a port
     *
     * @return the port entered by the user
     * @throws IOException if an I/O error occurs
     */
    private static int promptForPort() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter port (default: " + DEFAULT_PORT + ") : ");
        String portString = reader.readLine();
        if (portString.isEmpty()) {
            return DEFAULT_PORT;
        }
        int port = DEFAULT_PORT;
        try {
            Integer.parseInt(portString);
        } catch (NumberFormatException e) {
            MainClient.mainClientLogger.severe("Invalid port");
            System.exit(1);
        }
        if (isValidPort(port)) return port;

        MainClient.mainClientLogger.severe("Invalid port");
        System.exit(1);
        return port;
    }

    private static Client baseClient;

    public static void main(String[] args) throws IOException {
        serverIpAddress = promptForIpAddress();
        serverPort = promptForPort();
        try {

            //send message TODO: need to implement return from server
            Scanner scanner = new Scanner(System.in);

            //validation
            Account account = new Account("dummy account", "dummy");
            baseClient = new Client(account, (Inet4Address) serverIpAddress, serverPort);

            try (Socket client = new Socket(baseClient.getIpAddress(), baseClient.getPort())) {
                mainClientLogger.info("Successfully connected to server");
                //login
                //enter username
                sendLoginInfo(client, scanner);
                //enter password

                //if user does not exist add to DB

                //Display historic

                retrieveHistoric(client);

                //send message TODO: need to implement return from server
                chatRoomFunctionalities(client, scanner);
            }


        } catch (Exception e) {
            //throw new RuntimeException(e);
        }
    }

    private static void retrieveHistoric(Socket client) {
        mainClientLogger.info("Retrieving historic of messages... ");
        try {
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            out.writeByte(1);
            out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while (isRunning) {
                String message = in.readLine();
                if (message != null) {
                    if (message.equals("historic-end")) {
                        mainClientLogger.info("Retrieved end of historic of messages signal from server");
                        break;
                    } else {
                        System.out.println(message);
                    }
                }
            }
        } catch (IOException e) {
            MainClient.mainClientLogger.severe("IOException when trying to retrieve historic");
            isRunning = false;
        }
    }

    private static void sendLoginInfo(Socket client,Scanner scanner){
        //login
        System.out.println("Enter username :");
        String username = scanner.nextLine();
        username = username.replaceAll("\\s", "");
        username = username.replaceAll(":", "");

        System.out.println("Provide password:");
        String password = scanner.nextLine();
        password = password.replaceAll("\\s", "");
        password = password.replaceAll(":", "");

        Inet4Address address = (Inet4Address) client.getInetAddress();
        int port = client.getPort();
        Account account = new Account(username, password);
        baseClient = new Client(account, address, port);

        try {
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            encodeAndSend(3, out, username + " : " + password);
        }  catch (IOException e) {
            MainClient.mainClientLogger.severe("IOException when trying to send out to server");
            isRunning = false;
            System.exit(1);
        }
        //Display historic
        authentification(client, scanner);

    }


    ////////////////////////////////////////////////////////
    //Milestone in connection
    ////////////////////////////////////////////////////////

    /**
     * Chat room functionalities
     *
     * @param scanner the scanner to read user input
     */
    private static void chatRoomFunctionalities(Socket client, Scanner scanner) {
        isRunning = true;
        try {
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            listen(client);
            while (isRunning) {
                String message = scanner.nextLine();
                if (message.equals("exit")) {
                    isRunning = false;
                    out.writeByte(EXIT_CODE_MESSAGE);
                    out.flush();
                } else {
                    if ((!message.isEmpty()) && message.length() <= MAX_MESSAGE_LENGTH)
                        sendMessageToChat(out, message);
                }
            }
        } catch (IOException e) {
            MainClient.mainClientLogger.severe("IOException when trying to send message to chat");
            isRunning = false;
        }
    }

    private static void listen(Socket client) {
        new Thread(() -> {

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                while (isRunning) {
                    String message = in.readLine();
                    if (message != null)
                        System.out.println(message);
                }
            } catch (IOException e) {
                MainClient.mainClientLogger.severe("IOException when trying to listen to server messages");
                isRunning = false;
                System.exit(1);
            }
        }).start();
    }

    private static void sendMessageToChat(DataOutputStream out, String message) {
        encodeAndSend(2, out, message);
    }

    private static void encodeAndSend(int task, DataOutputStream out, String message) {
        try {
            out.writeByte(task);
            sendMessage(out, message);
        } catch (IOException e) {
            MainClient.mainClientLogger.warning("Error while encoding and sending message");
            MainClient.mainClientLogger.warning(e.getMessage());
        }
    }

    private static void sendMessage(DataOutputStream out, String message) {
        try {
            if (message.isEmpty())
                return;
            Message messageToSend = new Message(baseClient, LocalDateTime.now(), message);
            out.writeUTF(String.valueOf(messageToSend));
            out.flush(); // sends data
        } catch (IOException e) {
            MainClient.mainClientLogger.warning("Error while sending message");
            MainClient.mainClientLogger.warning(e.getMessage());
        }
    }

    private static void authentification(Socket client, Scanner scanner){
            try {
                boolean isNotAuthentified = true;
                DataInputStream in = new DataInputStream(client.getInputStream());
                while (isNotAuthentified) {
                    byte task = in.readByte();
                    switch (task) {
                        case '0' -> {
                            System.out.println("Bienvenue " + baseClient.getUsername());
                        }
                        case '1' -> {
                            System.out.println("Compte créé");
                            System.out.println("Bienvenue " + baseClient.getUsername());
                            isNotAuthentified = false;
                        }
                        case '2' -> {
                            System.out.println("Mot de passe invalide, veuiller réessayer.");
                            sendLoginInfo(client, scanner);
                        }
                    }
                }
            } catch (IOException e) {
                MainClient.mainClientLogger.severe("IOException when trying to read server return");
                isRunning = false;
                System.exit(1);
            }
    }
}