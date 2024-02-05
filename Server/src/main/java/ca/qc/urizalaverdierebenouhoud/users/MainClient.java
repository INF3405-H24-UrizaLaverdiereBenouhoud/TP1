package ca.qc.urizalaverdierebenouhoud.users;

import com.google.gson.JsonObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import org.json.JSONObject;

public class MainClient {

   // TestipAddress = "192.168.100.133";
    private static final int TestPort = 5003;

    private static Client baseClient;


    public static void main(String[] args)
    {
        try {



            Scanner scanner = new Scanner(System.in);
            System.out.println("Provide IP:");
              String ip = scanner.nextLine();

            //login
            System.out.println("Enter username :");
            String username = scanner.nextLine();

            System.out.println("Provide password:");
            String password = scanner.nextLine();

            //System.out.println("Provide IP:");
            //int port = Integer.parseInt(scanner.nextLine());

            //validation
            Inet4Address address = (Inet4Address) Inet4Address.getByName(ip);
            Account account = new Account(username, password);
            baseClient = new Client(account, address, TestPort);

            //send login info to server (account/client)9
            Socket client = new Socket(baseClient.getIpAddress(), baseClient.getPort());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            String[] loginInfo = { username, password, ip, Integer.toString(TestPort) };
            encodeAndSend(3, out, Arrays.toString(loginInfo));

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
        try(Socket client = new Socket(baseClient.getIpAddress(), baseClient.getPort()))
     {
         while (client.isConnected()) {
             DataOutputStream out = new DataOutputStream(client.getOutputStream());
             String message = scanner.nextLine();
             sendMessageToChat(out, message);
         }
        //client.close
        }
        catch (IOException e){

        throw new RuntimeException(e);

    }

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
            //throw new RuntimeException(e);
        }
    }
    private static void sendMessage (DataOutputStream out,String message)
    {
        try {
            out.writeUTF(message);
            out.flush(); // sends data
        } catch (IOException  e)
        {
            //throw new RuntimeException(e);
        }

    }



}
