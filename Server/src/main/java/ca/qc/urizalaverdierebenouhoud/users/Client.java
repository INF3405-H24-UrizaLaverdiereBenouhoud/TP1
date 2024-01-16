package ca.qc.urizalaverdierebenouhoud.users;

import java.net.Inet4Address;

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
        //login

        //validation

        //can send messages


    }
}
