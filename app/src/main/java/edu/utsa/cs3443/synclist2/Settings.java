package edu.utsa.cs3443.synclist2;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;

public class Settings extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_CODE = 100;
    private static final String PREFS_NAME = "AppPrefs";
    private static final String NOTIFICATION_KEY = "notifications_enabled";
    private static final String CHANNEL_ID = "default_channel";
    private static final int TEST_NOTIFICATION_ID = 1;

    private boolean notificationsEnabled;
    private SharedPreferences sharedPreferences;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Notification Manager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();

        // Load preferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        notificationsEnabled = sharedPreferences.getBoolean(NOTIFICATION_KEY, false);
        updateNotificationButtonText();

        // Button setup
//        Button button1 = findViewById(R.id.button);   // Sends test notification
        Button button2 = findViewById(R.id.button2);  // Logout
        Button button3 = findViewById(R.id.button3);  // Back
        Button button4 = findViewById(R.id.button4);  // Notifications toggle

// Button 1 logic is commented out because it only serves as a way to debug push notifications
//        button1.setOnClickListener(v -> {
//            if (notificationsEnabled) {
//                sendTestNotification();
//            } else {
//                Toast.makeText(this, "Enable notifications first", Toast.LENGTH_SHORT).show();
//            }
//        });

        button2.setOnClickListener(v -> {
            // Clear SharedPreferences to log out the user
            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Navigate to Login screen
            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        button3.setOnClickListener(v -> finish());
        button4.setOnClickListener(v -> toggleNotifications());
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("App Notifications");
        channel.enableLights(true);
        channel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(channel);
    }

    private void sendTestNotification() {
        // Check if notifications are enabled and we have permission (API 33+)
        if (!notificationsEnabled) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission needed", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Test Notification")
                .setContentText("Button 1 was pressed!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        notificationManager.notify(TEST_NOTIFICATION_ID, notification);
    }

    // Updates the notification toggle button text
    @SuppressLint("SetTextI18n")
    private void updateNotificationButtonText() {
        Button button4 = findViewById(R.id.button4);
        if (notificationsEnabled) {
            button4.setText("Notifications ON");
        } else {
            button4.setText("Notifications OFF");
        }
    }

    // Toggles notifications and handles permission (min API 33)
    private void toggleNotifications() {
        notificationsEnabled = !notificationsEnabled;

        // Save preference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_KEY, notificationsEnabled);
        editor.apply();

        updateNotificationButtonText();

        if (notificationsEnabled) {
            // Request permission on min API 33
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermission();
            } else {
                // On API 26-32 notifications work without explicit permission
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show();
        }
    }

    // Requests notification permission (min API 33)
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_CODE
            );
        } else {
            Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
        }
    }

    // Handles permission request result (min API 33)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                notificationsEnabled = true;
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            } else {
                notificationsEnabled = false;
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }

            updateNotificationButtonText();
            sharedPreferences.edit().putBoolean(NOTIFICATION_KEY, notificationsEnabled).apply();
        }
    }
}