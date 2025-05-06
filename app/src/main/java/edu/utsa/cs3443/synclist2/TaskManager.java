package edu.utsa.cs3443.synclist2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.utsa.cs3443.synclist2.model.Task;

public class TaskManager extends AppCompatActivity {

    private static final String CHANNEL_ID = "default_channel";
    private static final String PREFS_NAME = "AppPrefs";
    private static final String NOTIFICATION_KEY = "notifications_enabled";
    private static final int DELETE_NOTIFICATION_ID = 2;

    private RecyclerView taskRecyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private RecyclerView importantRecyclerView, schoolRecyclerView, workRecyclerView;
    private TaskAdapter importantAdapter, schoolAdapter, workAdapter;
    private List<Task> importantTasks, schoolTasks, workTasks;
    private NotificationManager notificationManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        // Initialize Notification Manager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();

        // Load preferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        importantRecyclerView = findViewById(R.id.importantRecyclerView);
        schoolRecyclerView = findViewById(R.id.schoolRecyclerView);
        workRecyclerView = findViewById(R.id.workRecyclerView);

        importantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        schoolRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        workRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        importantTasks = new ArrayList<>();
        schoolTasks = new ArrayList<>();
        workTasks = new ArrayList<>();

        importantAdapter = new TaskAdapter(importantTasks);
        schoolAdapter = new TaskAdapter(schoolTasks);
        workAdapter = new TaskAdapter(workTasks);

        importantRecyclerView.setAdapter(importantAdapter);
        schoolRecyclerView.setAdapter(schoolAdapter);
        workRecyclerView.setAdapter(workAdapter);

        // Load tasks from CSV here
        loadTasksFromCSV();

        Button addButton = findViewById(R.id.add_button);
        ImageButton settingsButton = findViewById(R.id.settingsButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskManager.this, AddTask.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskManager.this, Settings.class);
                startActivity(intent);
            }
        });

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            List<Task> toDelete = new ArrayList<>();
            toDelete.addAll(importantAdapter.getSelectedTasks());
            toDelete.addAll(schoolAdapter.getSelectedTasks());
            toDelete.addAll(workAdapter.getSelectedTasks());

            if (!toDelete.isEmpty()) {
                importantTasks.removeAll(toDelete);
                schoolTasks.removeAll(toDelete);
                workTasks.removeAll(toDelete);
                saveTasksToCSV();
                importantAdapter.notifyDataSetChanged();
                schoolAdapter.notifyDataSetChanged();
                workAdapter.notifyDataSetChanged();

                // Send success notification if notifications are enabled
                sendDeleteSuccessNotification();
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
    }

    private void sendDeleteSuccessNotification() {
        boolean notificationsEnabled = sharedPreferences.getBoolean(NOTIFICATION_KEY, false);

        if (!notificationsEnabled) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("SyncList")
                .setContentText("Success! You completed a task!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        notificationManager.notify(DELETE_NOTIFICATION_ID, notification);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasksFromCSV();     // Reload tasks from CSV
    }

    private void loadTasksFromCSV() {
        // clears previous data
        importantTasks.clear();
        schoolTasks.clear();
        workTasks.clear();

        String filename = "tasks.csv";
        try {
            FileInputStream fis = openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);  // -1 keeps empty fields
                if (parts.length >= 4) {
                    String title = parts[0];
                    String category = parts[1];
                    String dueDate = parts[2];
                    String note = parts[3];
                    Task task = new Task(title, category, dueDate, note);

                    switch (category.toLowerCase()) {
                        case "important":
                            importantTasks.add(task);
                            break;
                        case "school":
                            schoolTasks.add(task);
                            break;
                        case "work":
                            workTasks.add(task);
                            break;
                    }
                }
            }
            reader.close();

            importantAdapter.notifyDataSetChanged();
            schoolAdapter.notifyDataSetChanged();
            workAdapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveTasksToCSV() {
        String filename = "tasks.csv";
        try {
            FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            List<Task> allTasks = new ArrayList<>();
            allTasks.addAll(importantTasks);
            allTasks.addAll(schoolTasks);
            allTasks.addAll(workTasks);
            for (Task task : allTasks) {
                writer.write(task.getTitle() + "," + task.getCategory() + "," + task.getDueDate() + "," + task.getNote());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}