package ca.qc.urizalaverdierebenouhoud.users;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Inet4Address;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Account {
    private static final Logger accountLogger = Logger.getLogger(Account.class.getName());
    protected static List<Account> accounts = new ArrayList<>();
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;

    public static Client login(String username, String password) throws Exception {
        // TODO Implement rejection if the username/password combo is invalid
        // TODO Implement method login for Account
        // TODO Handle exceptions
        return new Client(new Account(username, password), (Inet4Address) Inet4Address.getByName("192.168.0.199"), 25565);
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

