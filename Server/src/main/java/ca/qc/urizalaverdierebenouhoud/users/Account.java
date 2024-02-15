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

    private static File accountsFile;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public static void setMessagesFile(File accountsFile) {
        Account.accountsFile = accountsFile;
    }

    /**
     *  Takes the accounts defined into the provided JSON file and loads them into
     *  the accounts ArrayList.
     *  Will make the program exit with 1 code if file couldn't be loaded
     */

    public static void loadAccounts() {
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
            fileContent = new String(Files.readAllBytes(Paths.get(accountsFile.getAbsolutePath())));
        } catch (IOException e) {
            Account.accountLogger.severe("File" + accountsFile.getAbsolutePath() + " couldn't be read");
            System.exit(1);
        }

        Account[] loadedAccounts = gson.fromJson(fileContent, Account[].class);
        Account.accounts = new ArrayList<>(Arrays.asList(loadedAccounts));
    }

    public static List<Account> getAccounts(){
        return accounts;
    }
    public static void addAccount(Account clientAccount){
        accounts.add(clientAccount);
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

