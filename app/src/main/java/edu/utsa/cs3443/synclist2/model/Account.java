package edu.utsa.cs3443.synclist2.model;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Account {
    // Model fields
    private String email;
    private String username;
    private String password;

    // CSV file name for account storage
    private static final String FILENAME = "Accounts.csv";

    // Constructor
    public Account(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // Getters
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    // Format account as a CSV line: email,username,password
    @Override
    public String toString() {
        return email + "," + username + "," + password;
    }

    // Copy the Accounts.csv from Assets to internal storage if it doesn't already exist.
    private static void copyAccountsFile(Context context) {
        File file = new File(context.getFilesDir(), FILENAME);
        if (file.exists()) {
            return; // File already exists, no need to copy.
        }
        AssetManager assetManager = context.getAssets();
        try (InputStream in = assetManager.open(FILENAME);
             FileOutputStream out = context.openFileOutput(FILENAME, Context.MODE_PRIVATE)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load all accounts from the CSV file in internal storage.
    public static List<Account> loadAccounts(Context context) {
        // Ensure the file exists by copying it from Assets if needed.
        copyAccountsFile(context);
        List<Account> accounts = new ArrayList<>();
        File file = new File(context.getFilesDir(), FILENAME);
        if (!file.exists()) {
            return accounts;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // CSV format: email,username,password
                String[] tokens = line.split(",");
                if (tokens.length >= 3) {
                    accounts.add(new Account(tokens[0], tokens[1], tokens[2]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    // checkEmail(): Returns true if the provided email is not found (i.e. is unique), false if already used.
    public static boolean checkEmail(Context context, String email) {
        List<Account> accounts = loadAccounts(context);
        for (Account acc : accounts) {
            if (acc.getEmail().equalsIgnoreCase(email)) {
                return false; // Email already in use.
            }
        }
        return true; // Email is unique.
    }

    // Save a new account by appending it to the CSV file.
    public static void saveAccount(Context context, Account account) {
        // Ensure the file exists first.
        copyAccountsFile(context);
        File file = new File(context.getFilesDir(), FILENAME);
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.append(account.toString()).append("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Account findUsername(Context context, String username) {
        List<Account> accounts = loadAccounts(context);
        for (Account acc : accounts) {
            if (acc.getUsername().trim().equalsIgnoreCase(username)) {
                return acc;
            }
        }
        return null;
    }
}
