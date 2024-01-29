package ca.qc.urizalaverdierebenouhoud.users;
import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Scanner;

public class MainClient {
   // TestipAddress = "192.168.100.133";
    private static final int TestPort = 5003;

    private static Client baseClient;



    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Provide IP:");
            String ip = scanner.nextLine();

            //login
            //enter username

            //enter password

            //validation
            Inet4Address address = (Inet4Address) Inet4Address.getByName(ip);
            Account account = new Account("dummy account", "dummy");
            baseClient = new Client(account,address, TestPort);
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
    private static void chatRoomFunctionalities(Scanner scanner) {
        try (Socket client = new Socket(baseClient.getIpAddress(), baseClient.getPort())) {
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            listen(client);
            while (client.isConnected()) {
                String message = scanner.nextLine();
                sendMessageToChat(out, message);
            }
            //client.close
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void listen(Socket client) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader in;
                try {
                    while (true) {
                        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String message = in.readLine();
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    ////////////////////////////////////////////////////////
    //Encodage pour envoi
    ////////////////////////////////////////////////////////
    // Task methods in Main
    /////////////////////////////////////
    private static void sendMessageToChat(DataOutputStream out, String message) {
        encodeAndSend(2, out, message);
    }

    /////////////////////////////////////
    // called methods for sending
    /////////////////////////////////////
    private static void encodeAndSend(int task, DataOutputStream out, String message) {
        try {
            out.writeByte(task);
            sendMessage(out, message);
        } catch (IOException e) {
            //throw new RuntimeException(e);
        }
    }

    private static void sendMessage(DataOutputStream out, String message) {
        try {
            out.writeUTF(message);
            out.flush(); // sends data
        } catch (IOException e) {
            //throw new RuntimeException(e);
        }
    }
}
