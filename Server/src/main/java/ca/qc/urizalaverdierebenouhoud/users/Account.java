package ca.qc.urizalaverdierebenouhoud.users;

import ca.qc.urizalaverdierebenouhoud.message.Message;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.*;
import java.net.Inet4Address;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class Account {
    private static final Logger accountLogger = Logger.getLogger(Account.class.getName());
    protected static List<Account> accounts = new ArrayList<>();
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;

    /**
     * Takes the accounts defined into the provided JSON file and loads them into
     * @param username The username of the account to find
     * @param password The password of the account to find
     * @param ipAddress The IP Address of the client
     * @param port The port of the client
     * @return The Client object corresponding to the account
     * @throws InvalidUsernamePasswordComboException If the username/password combo is invalid
     */
    public static byte login(String username, String password, Inet4Address ipAddress, int port) {
        try {
            for (Account account : Account.accounts) {
                if (account.getUsername().equals(username)) {
                    if (account.getPassword().equals(password)) {
                        System.out.println("hi person");
                        return '0';
                    } else {
                        System.out.println("bad password");
                        return '2';
                    }
                }
            }
            accounts.add(new Account(username, password));
            System.out.println("newAccount");
            return '1';
        } catch (Exception e) {}
        return '3';
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     *  Takes the accounts defined into the provided JSON file and loads them into
     *  the accounts ArrayList.
     *  Will make the program exit with 1 code if file couldn't be loaded
     * @param file The JSON file containing the accounts to load.
     */
    public static void loadAccounts(File file) {
        /*
            Define an Account as the following JSON Object:
            {
                "username": "cohenmaud",
                "password": "V1vePoly!"
            }
         */
        Gson gson = new Gson();

        String fileContent = null;
        try {
            fileContent = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        } catch (IOException e) {
            Account.accountLogger.severe("File" + file.getAbsolutePath() + " couldn't be read");
            System.exit(1);
        }

        Account[] loadedAccounts = gson.fromJson(fileContent, Account[].class);
        Account.accounts = new ArrayList<>(Arrays.asList(loadedAccounts));
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

