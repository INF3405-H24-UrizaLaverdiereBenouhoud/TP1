package ca.qc.urizalaverdierebenouhoud.users;

import java.io.File;
import java.net.Inet4Address;
import java.util.ArrayList;

public class Account {

    public static ArrayList<Account> accounts = new ArrayList<Account>();
    private String username, password;

    public static Client login(String username, String password) throws Exception {
        // TODO Implement rejection if the username/password combo is invalid
        // TODO Implement method login for Account
        // TODO Handle exceptions
        return new Client(new Account(username, password), (Inet4Address) Inet4Address.getByName("192.168.0.199"), 25565);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *  Takes the accounts defined into the provided JSON file and loads them into
     *  the accounts ArrayList.
     * @param file The JSON file containing the accounts to load.
     */
    public static void loadAccounts(File file) {
        // TODO Implement loadAccounts method
        /*
            Define an Account as the following JSON Object:
            {
                "username": "cohenmaud",
                "password": "V1vePoly!"
            }
         */
    }

    @Override
    public String toString() {
        return this.getUsername();
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
