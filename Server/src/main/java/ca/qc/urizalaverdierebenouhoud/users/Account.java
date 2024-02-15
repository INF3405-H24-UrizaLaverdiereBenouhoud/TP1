package ca.qc.urizalaverdierebenouhoud.users;

import ca.qc.urizalaverdierebenouhoud.logger.INF3405Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class Account {
    private static final INF3405Logger accountLogger = new INF3405Logger("Account", Account.class.getName());
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
    public static void setAccountFile(File accountsFile) {
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

    @Override
    public String toString() {
        return this.getUsername();
    }

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private static void saveAccounts() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        String json = gson.toJson(Account.accounts);
        try {
            Files.write(Paths.get(Account.accountsFile.getAbsolutePath()), json.getBytes());
        } catch (IOException e) {
            Account.accountLogger.severe("File" + Account.accountsFile.getAbsolutePath() + " couldn't be written");
            System.exit(1);
        }
    }

    public static void saveAccount(Account account) {
        Account.accounts.add(account);
        Account.saveAccounts();
        accountLogger.info(account.getUsername() + "'s account saved");
    }
}

