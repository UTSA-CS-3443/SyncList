package edu.utsa.cs3443.synclist2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.utsa.cs3443.synclist2.model.Account;

public class Login extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button firstCreateAccountButton;
    private CheckBox rememberMeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in (SharedPreferences)
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // If logged in, skip Login and go to TaskManager
            Intent intent = new Intent(Login.this, TaskManager.class);
            startActivity(intent);
            finish();
            return;  // Prevent further execution
        }

        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        firstCreateAccountButton = findViewById(R.id.firstCreateAccountButton);
        rememberMeCheckBox = findViewById(R.id.checkBox);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity();
            }
        });

        // Set onClick listener for the Create Account button
        firstCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cIntent = new Intent(Login.this, CreateAccount.class);
                startActivity(cIntent);
            }
        });
    }

    private void launchActivity() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        login(username, password);
    }

    private void login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_LONG).show();
            return;
        }
        // Look for an account with the given username
        Account account = Account.findUsername(this, username);
        if (account == null) {
            Toast.makeText(this, "User not found, try again", Toast.LENGTH_LONG).show();
        } else {
            if (account.getPassword().equals(password)) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_LONG).show();

                // Save login state if Remember Me is checked
                if (rememberMeCheckBox.isChecked()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                }

                // Navigate to the main screen
                Intent mainIntent = new Intent(Login.this, TaskManager.class);
                startActivity(mainIntent);
                finish();
            } else {
                Toast.makeText(this, "Invalid password", Toast.LENGTH_LONG).show();
            }
        }
    }
}