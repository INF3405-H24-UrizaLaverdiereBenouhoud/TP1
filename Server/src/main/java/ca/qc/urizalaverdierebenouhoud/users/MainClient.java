package ca.qc.urizalaverdierebenouhoud.users;

import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MainClient {
    // TestipAddress = "192.168.100.133";
    private static final int TestPort = 5003;

    private static Client baseClient;
    private static boolean isRunning = true;

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Provide IP(default: 5003) :");
            String ip = scanner.nextLine();

            //login
            //enter username

            //enter password

            //validation
            Inet4Address address = (Inet4Address) Inet4Address.getByName(ip);
            Account account = new Account("dummy account", "dummy");
            baseClient = new Client(account, address, TestPort);
            //if user does not exist add to DB

            //Display historic

            //send message TODO: need to implement return from server
            chatRoomFunctionalities(scanner);

        } catch (Exception e) {
            //throw new RuntimeException(e);
        }
    }

    ////////////////////////////////////////////////////////
    //Milestone in connection
    ////////////////////////////////////////////////////////
    private static void chatRoomFunctionalities(Scanner scanner) {
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
    private static void sendMessageToChat(DataOutputStream out, String message) throws IOException {
        encodeAndSend(2, out, message);
    }

    /////////////////////////////////////
    // called methods for sending
    /////////////////////////////////////
    private static void encodeAndSend(int task, DataOutputStream out, String message) throws IOException {
            out.writeByte(task);
            sendMessage(out, message);
    }

    private static void sendMessage(DataOutputStream out, String message) {
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
