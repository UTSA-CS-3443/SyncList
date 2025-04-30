package edu.utsa.cs3443.synclist2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.utsa.cs3443.synclist2.model.Account;

public class CreateAccount extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText usernameEditText;
    private Button createAccountButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        createAccountButton = findViewById(R.id.createAccountButton);
        backButton = findViewById(R.id.backButton);

        createAccountButton.setOnClickListener(view -> createAccount());

        backButton.setOnClickListener(view -> {
            finish(); // Returns to the previous screen
        });
    }

    private void createAccount() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
        } else if (!Account.checkEmail(this, email)) {
            Toast.makeText(this, "Email already in use", Toast.LENGTH_LONG).show();
        } else {
            Account newAccount = new Account(email, username, password);
            Account.saveAccount(this, newAccount);
            Toast.makeText(this, "Account Created for: " + username, Toast.LENGTH_LONG).show();

            // Navigate to TaskManager
            Intent mainIntent = new Intent(CreateAccount.this, TaskManager.class);
            startActivity(mainIntent);
            finish();
        }

    }
}