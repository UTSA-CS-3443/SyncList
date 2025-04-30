package edu.utsa.cs3443.synclist2.model;

/**
 * Represents a task with a title, category, due date, and a note.
 * This class is used to encapsulate the details of a task that the user creates.
 *
 * @author PFF702
 */
public class Task {

    private String title;
    private String category;
    private String dueDate;
    private String note;

    /**
     * Constructs a new Task object with the specified title, category, due date, and note.
     *
     * @param title The title of the task.
     * @param category The category of the task (e.g., "Work", "Personal").
     * @param dueDate The due date of the task in MM/DD/YYYY format.
     * @param note A note or additional information about the task.
     */
    public Task(String title, String category, String dueDate, String note) {
        this.title = title;
        this.category = category;
        this.dueDate = dueDate;
        this.note = note;
    }

    /**
     * Gets the title of the task.
     *
     * @return The title of the task.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the category of the task.
     *
     * @return The category of the task.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Gets the due date of the task.
     *
     * @return The due date of the task in MM/DD/YYYY format.
     */
    public String getDueDate() {
        return dueDate;
    }

    /**
     * Gets the note associated with the task.
     *
     * @return The note associated with the task.
     */
    public String getNote() {
        return note;
    }
}
