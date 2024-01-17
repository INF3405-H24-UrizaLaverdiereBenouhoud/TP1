package ca.qc.urizalaverdierebenouhoud.users;

import javax.imageio.IIOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String username;
    private Inet4Address ipAddress;
    private int port;
    private static String TestipAddress = "192.168.100.133";
    private static int TestPort = 5003;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Inet4Address getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(Inet4Address ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    // TODO Implement a toJSON method and follow this pattern
    /*
        Define a Client as the following JSON Object:
        {
            "username": "cohenmaud",
            "ipAddress": "192.168.0.2",
            "port": 46202,
        }
     */

    @Override
    public String toString() {
        return this.getUsername() + " - " + this.getIpAddress().toString().replace("/", "") + ":" + this.getPort();
    }

    public Client(Account account, Inet4Address ipAddress, int port) {
        this.username = account.getUsername();
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public static void main(String[] args)
    {
        try {

            Scanner scanner = new Scanner(System.in);
            //login
            //enter username

            //enter password

            //validation
                //if user does not exist add to DB

            //Display historic

            //send message TODO: need to implement return from server
          chatRoomFunctionalities(scanner);
            }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void chatRoomFunctionalities(Scanner scanner)
    {
        while(true) {
            try {
            Socket client = new Socket(TestipAddress,TestPort);
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            String message = scanner.nextLine();
            sendMessageToChat(out,message);
            client.close();} catch (IOException e){
                throw new RuntimeException(e);
            }
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
            throw new RuntimeException(e);
        }
    }
    private static void sendMessage (DataOutputStream out,String message)
    {
        try {
            out.writeUTF(message);
            out.flush(); // sends data
        } catch (IOException  e)
        {
            throw new RuntimeException(e);
        }

    }


}
