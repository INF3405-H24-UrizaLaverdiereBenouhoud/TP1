package ca.qc.urizalaverdierebenouhoud.users;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String username;
    private Inet4Address ipAddress;
    private int port;

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
           String TestipAddress = "192.168.100.133";
           int TestPort = 5003;

            Scanner scanner = new Scanner(System.in);
            //login
            //enter username

            //enter password

            //validation
                //if user does not exist add to DB

            //send message TODO: need to implement return from server
            while(true) {
                Socket client = new Socket(TestipAddress,TestPort);
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                String message = scanner.nextLine();
                out.writeByte(2);
                out.writeUTF(message);
                out.flush(); // sends data
                client.close();
            }
            }
        catch (Exception e)
        {
            System.err.println(e);
        }

        //can send messages

    }

}
