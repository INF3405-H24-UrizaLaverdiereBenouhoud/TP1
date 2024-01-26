package ca.qc.urizalaverdierebenouhoud;

import ca.qc.urizalaverdierebenouhoud.users.Account;
import ca.qc.urizalaverdierebenouhoud.users.Client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainClient {
    private static final Logger mainClientLogger = Logger.getLogger(MainClient.class.getName());

    private static final String DEFAULT_IP_ADDRESS = "127.0.0.1"; // Default address is localhost for local testing
    private static final int DEFAULT_PORT = 5003;

    private static Client baseClient;

    /**
     * Returns the bit associated with the task
     * @param task the task to get the bit for
     * @return the bit associated with the task, -999 if the task is not found
     */
    private static int getTaskBit(ChatTask task) {
        switch (task) {
            case LOGIN -> {
                return 0;
            }
            case SEND_RECENT_HISTORY -> {
                return 1;
            }
            case SEND_MESSAGE -> {
                return 2;
            }
            default -> {
                MainClient.mainClientLogger.log(Level.WARNING, "Could not find task bit for task {0}", task);
                return -999;
            }
        }
    }
    public static void main(String[] args)
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Provide IP:");
            String ip = scanner.nextLine();

            if(ip.isEmpty())
            {
                ip = DEFAULT_IP_ADDRESS;
            }

            //login
            //enter username

            //enter password

            //validation
            Inet4Address address = (Inet4Address) InetAddress.getByName(ip);
            Account account = new Account("dummy account", "dummy");
            baseClient = new Client(account,address, DEFAULT_PORT);
            //if user does not exist add to DB

            //Display historic

            //send message TODO: need to implement return from server
            chatRoomFunctionalities(scanner);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e); // TODO Put logger with severe level and appropriate message, then quit with non-zero exit code
        }
    }

    /**
     *  Event loop method to send messages to the chat
     * @param scanner the scanner to get the user input
     */
    private static void chatRoomFunctionalities(Scanner scanner)
    {
        while(true)
        {
            try (Socket client = new Socket(baseClient.getIpAddress(), baseClient.getPort()))
            {
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                String message = scanner.nextLine();
                if (message.equals("quit"))
                {
                    break;
                }
                sendMessageToChat(out,message);
            }
            catch (IOException e)
            {
                MainClient.mainClientLogger.severe("An error occured while managing the chat room " + e.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     *  Sends a message to the server to send to the chat
     *  Abstraction of {@link MainClient#encodeAndSend(ChatTask, DataOutputStream, String)}
     * @param out the output stream to send the message to
     * @param message the message to send
     */
    private static void sendMessageToChat(DataOutputStream out, String message)
    {
        encodeAndSend(ChatTask.SEND_MESSAGE,out, message);
        MainClient.mainClientLogger.log(Level.INFO, "Sent message to chat: {0}", message);
    }

    /**
     * Encodes the task and sends the message to the server
     *  Sends a first byte to the server to indicate the task
     *  Then sends the UTF-8 encoded message to the server
     * @param task the task to encode, see {@link ChatTask}
     * @param out the output stream to send the message to
     * @param message the message to send
     */
    private static void encodeAndSend(ChatTask task, DataOutputStream out, String message)
    {
        try
        {
            out.writeByte(getTaskBit(task));
            sendMessage(out,message);
        }
        catch (IOException e)
        {
            MainClient.mainClientLogger.severe("An error occurred while sending message to server " + e.getMessage());
        }
    }

    /**
     * Sends a UTF-8 encoded message to the server
     * @param out the output stream to send the message to
     * @param message the message to send
     */
    private static void sendMessage(DataOutputStream out, String message)
    {
        try
        {
            out.writeUTF(message);
            out.flush();
        }
        catch (IOException e)
        {
            MainClient.mainClientLogger.severe("An error occurred while sending message to server " + e.getMessage());
        }
    }



}
