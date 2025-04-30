package edu.utsa.cs3443.synclist2;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import edu.utsa.cs3443.synclist2.model.Task;

/**
 * Activity class responsible for creating a new task by entering its details.
 * This includes task title, category, due date, and notes.
 * The user can select a due date using a DatePickerDialog, choose a category from a Spinner,
 * and save the task information by clicking the submit button.
 *
 * @author PFF702
 */
public class AddTask extends AppCompatActivity {

    private EditText taskTitleEditText, dueDateEditText, noteEditText;
    private Spinner categorySpinner;
    private Button submitButton, cancelButton;
    private Calendar calendar;

    /**
     * Called when the activity is created. Initializes the UI components and sets up the
     * event listeners for the DatePicker, Submit, and Cancel buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Initialize UI components
        taskTitleEditText = findViewById(R.id.taskTitle);
        categorySpinner = findViewById(R.id.categorySpinner);
        dueDateEditText = findViewById(R.id.dueDate);
        noteEditText = findViewById(R.id.note);
        submitButton = findViewById(R.id.submitButton);
        cancelButton = findViewById(R.id.cancelButton);

        calendar = Calendar.getInstance();

        // Set up Category Spinner with predefined categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.task_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Set up Date Picker dialog for selecting due date
        dueDateEditText.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddTask.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Set the selected date in dueDateEditText in MM/DD/YYYY format
                        dueDateEditText.setText((selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        // Handle Submit button click to save the task
        submitButton.setOnClickListener(v -> saveTask());

        // Handle Cancel button click to close the activity
        cancelButton.setOnClickListener(v -> finish());
    }

    /**
     * Validates the task input fields, creates a new Task object, and saves it (currently placeholder for actual saving logic).
     * If the required fields are empty (title or due date), it shows an error on the respective fields.
     */
    private void saveTask() {
        String title = taskTitleEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String dueDate = dueDateEditText.getText().toString().trim();
        String note = noteEditText.getText().toString().trim();

        // Validate input fields
        if (title.isEmpty() || dueDate.isEmpty()) {
            taskTitleEditText.setError("Title is required");
            dueDateEditText.setError("Due date is required");
            return;
        }

        // Create a new Task object with the input values
        Task newTask = new Task(title, category, dueDate, note);

        // Save the task to CSV
        saveTaskToCSV(newTask);

        // Close the activity after saving
        finish();
    }

    public void saveTaskToCSV(Task task) {
        String filename = "tasks.csv";
        String data = task.getTitle() + "," + task.getCategory() + "," + task.getDueDate() + "," + task.getNote() + "\n"; // Append a new line for each task

        try (FileOutputStream fos = openFileOutput(filename, Context.MODE_APPEND)) {
            fos.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}