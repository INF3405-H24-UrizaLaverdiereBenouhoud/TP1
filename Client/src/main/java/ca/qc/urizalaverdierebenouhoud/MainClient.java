package ca.qc.urizalaverdierebenouhoud;

import ca.qc.urizalaverdierebenouhoud.users.Account;
import ca.qc.urizalaverdierebenouhoud.users.Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MainClient {

    private static final String DEFAULT_IP_ADDRESS = "0.0.0.0";
    private static final int DEFAULT_PORT = 5003;
    private static boolean isRunning = true;
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
            System.err.println("Invalid IP address");
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
        System.out.print("Enter port (default: 5003) : ");
        String portString = reader.readLine();
        if (portString.isEmpty()) {
            return DEFAULT_PORT;
        }
        int port = Integer.parseInt(portString);
        if(!isValidPort(port)) {
            System.err.println("Invalid port");
            System.exit(1);
        }
        return port;
    }
    private static Client baseClient;

    public static void main(String[] args) throws IOException {
        InetAddress serverIpAddress = promptForIpAddress();
        int serverPort = promptForPort();

        try {

            Scanner scanner = new Scanner(System.in);

            //login
            //enter username

            //enter password

            //validation
            Account account = new Account("dummy account", "dummy");
            baseClient = new Client(account, (Inet4Address) serverIpAddress, serverPort);
            //if user does not exist add to DB

            //Display historic

            //send message TODO: need to implement return from server
            chatRoomFunctionalities(scanner);
        }
        catch (Exception e)
        {
            //throw new RuntimeException(e);
        }
    }


    ////////////////////////////////////////////////////////
    //Milestone in connection
    ////////////////////////////////////////////////////////
    private static void chatRoomFunctionalities(Scanner scanner)
    {
        try (Socket client = new Socket(baseClient.getIpAddress(), baseClient.getPort())) {
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            listen(client);
            while (isRunning) {
                String message = scanner.nextLine();
                if(message.equals("exit")) {
                    isRunning = false;
                    out.writeByte(4);
                    out.flush();
                    client.close();
                }
                else {
                    if((!message.isEmpty())&&message.length()<=200)
                        sendMessageToChat(out, message);
                }}
            //client.close
        } catch (IOException e) {
            isRunning = false;
        }
    }

    private static void listen(Socket client) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader in;
                try {
                    while (isRunning) {
                        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String message = in.readLine();
                        if(message != null)
                            System.out.println(message);
                    }
                } catch (IOException e) {
                    isRunning = false;
                }
            }
        }).start();
    }

    ////////////////////////////////////////////////////////
    //Encodage pour envoi
    ////////////////////////////////////////////////////////
    // Task methods in Main
    /////////////////////////////////////
    private static void sendMessageToChat(DataOutputStream out, String message)
    {
        encodeAndSend(2,out, message);
    }
    /////////////////////////////////////
    // called methods for sending
    /////////////////////////////////////
    private static void encodeAndSend(int task, DataOutputStream out, String message)
    {
        try {
            out.writeByte(task);
            sendMessage(out,message);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
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
            System.out.println("Message failed to send");
            System.out.println(e);
        }
    }



}
