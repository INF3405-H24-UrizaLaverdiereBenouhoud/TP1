package ca.qc.urizalaverdierebenouhoud;

import ca.qc.urizalaverdierebenouhoud.logger.INF3405Logger;
import ca.qc.urizalaverdierebenouhoud.users.Account;
import ca.qc.urizalaverdierebenouhoud.users.Client;
import ca.qc.urizalaverdierebenouhoud.message.Message;

import java.io.*;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.net.Inet4Address;

public class MainClient {

    private static final INF3405Logger mainClientLogger = new INF3405Logger("MainClient", null);

    private static final String DEFAULT_IP_ADDRESS = "0.0.0.0";
    private static final int DEFAULT_PORT = 5003;
    public static final int MAX_MESSAGE_LENGTH = 200;
    public static final int EXIT_CODE_MESSAGE = 4;
    private static boolean isRunning = true;

    private static InetAddress serverIpAddress;
    private static int serverPort;
    /**
     *  Checks if the given string is a valid IP address
     * @param ipAddress the string to check
     * @return true if the string is a valid IP address, false otherwise
     */
    private static boolean isValidIpAddress(String ipAddress) {
        String[] tokens = ipAddress.split("\\.");
        if (tokens.length != 4) {
            return false;
        }
        for (String token : tokens) {
            int tokenInt = Integer.parseInt(token);
            if (tokenInt < 0 || tokenInt > 255) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given port is valid (between 5000 and 5050)
     * @param port the port to check
     * @return true if the port is valid, false otherwise
     */
    private static boolean isValidPort(int port) {
        return port >= 5000 && port <= 5050;
    }

    /**
     * Prompts the user for an IP address
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
        if(!isValidIpAddress(ipAddress)) {
            MainClient.mainClientLogger.severe("Invalid IP address");
            System.exit(1);
        }
        return InetAddress.getByName(ipAddress);
    }

    /**
     * Prompts the user for a port
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
            sendLoginInfo();
            //send message TODO: need to implement return from server
            Scanner scanner = new Scanner(System.in);
            chatRoomFunctionalities(scanner);
        }
        catch (Exception e)
        {
            //throw new RuntimeException(e);
        }
    }

    private static void sendLoginInfo() throws IOException {

        Scanner scanner = new Scanner(System.in);
        //login
        System.out.println("Enter username :");
        String username = scanner.nextLine();

        System.out.println("Provide password:");
        String password = scanner.nextLine();

        Socket client = new Socket(serverIpAddress, serverPort);
        Inet4Address address = (Inet4Address) client.getInetAddress();
        int port = client.getPort();
        Account account = new Account(username, password);
        baseClient = new Client(account, address, port);

        //validation
        //send login info to server (account/client)
        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        encodeAndSend(3, out, username + " : " + password);

        //Display historic
        authentification(client);
    }


    ////////////////////////////////////////////////////////
    //Milestone in connection
    ////////////////////////////////////////////////////////
    // private static void chatRoomFunctionalities()
    /**
     *  Chat room functionalities
     * @param scanner the scanner to read user input
     */
    private static void chatRoomFunctionalities(Scanner scanner)
    {
        try (Socket client = new Socket(baseClient.getIpAddress(), baseClient.getPort())) {
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            listen(client);
            while (isRunning) {
                String message = scanner.nextLine();
                if(message.equals("exit")) {
                    isRunning = false;
                    out.writeByte(EXIT_CODE_MESSAGE);
                    out.flush();
                } else {
                    if((!message.isEmpty()) && message.length() <= MAX_MESSAGE_LENGTH)
                        sendMessageToChat(out, message);
                }
            }
        } catch (IOException e) {
            MainClient.mainClientLogger.severe("Server is down");
            isRunning = false;
        }
    }

    private static void listen(Socket client) {
        new Thread(() -> {
            BufferedReader in;
            try {
                while (isRunning) {
                    in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String message = in.readLine();
                    if(message != null)
                        MainClient.mainClientLogger.info(message);
                }
            } catch (IOException e) {
                MainClient.mainClientLogger.severe("Server is down");
                isRunning = false;
            }
        }).start();
    }

    private static void sendMessageToChat(DataOutputStream out, String message)
    {
        encodeAndSend(2,out, message);
    }

    private static void encodeAndSend(int task, DataOutputStream out, String message)
    {
        try {
            out.writeByte(task);
            sendMessage(out,message);
        }
        catch (IOException e)
        {
            MainClient.mainClientLogger.warning("Error while encoding and sending message");
            MainClient.mainClientLogger.warning(e.getMessage());
        }
    }
    private static void sendMessage (DataOutputStream out,String message)
    {
        try {
            if (message.isEmpty())
                return;
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy '@' HH:mm:ss");
            String formattedDate = now.format(formatter);
            String stringToSend = "[" + baseClient.getUsername() + " - " + baseClient.getIpAddress().toString() + " : "
                    + baseClient.getPort() + "-" + formattedDate + "]: " + message;
            out.writeUTF(stringToSend);
            out.flush(); // sends data
        } catch (IOException e) {
            MainClient.mainClientLogger.warning("Error while sending message");
            MainClient.mainClientLogger.warning(e.getMessage());
        }
    }
    private static void authentification(Socket client) throws IOException {
        new Thread(() -> {
            try {
                while (isRunning) {
                    DataInputStream in = new DataInputStream(client.getInputStream());
                    Byte task = in.readByte();
                    System.out.println(in);

                    // String history = in.readLine();
                    switch (task) {
                        case '0' -> {
                            System.out.println("Bienvenue " + baseClient.getUsername());
                            displayHistory(in.readUTF(), client);
                        }
                        case '1' -> {
                            System.out.println("Compte créé");
                            System.out.println("Bienvenue " + baseClient.getUsername());
                            displayHistory(in.readUTF(), client);
                        }
                        case '2' -> {
                            System.out.println("Mot de passe invalide, veuiller réessayer.");
                            sendLoginInfo();
                        }
                    }
                }
            } catch (IOException e) {
                isRunning = false;
            }
        }).start();
    }
    private static void displayHistory(String history, Socket socket) {
       try {
           String[] messagesOut = history.split(" , ");
           for (String message : messagesOut) {
               System.out.println(message);
           }
       } catch (Exception e) {}
    }

}
