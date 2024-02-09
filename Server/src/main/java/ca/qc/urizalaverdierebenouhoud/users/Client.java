package ca.qc.urizalaverdierebenouhoud.users;

import java.net.Inet4Address;

public class Client {
    private final String username;
    private final Inet4Address ipAddress;
    private final int port;


    public String getUsername() {
        return username;
    }

    public Inet4Address getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

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

    /**
     * Constructor for the Client class
     * @param account the account of the client
     * @param ipAddress the IP address of the client
     * @param port the port of the client
     */
    public Client(Account account, Inet4Address ipAddress, int port) {
        this.username = account.getUsername();
        this.ipAddress = ipAddress;
        this.port = port;
    }




}
