package edu.utsa.cs3443.synclist2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.utsa.cs3443.synclist2.model.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private List<Task> selectedTasks = new ArrayList<>();

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);  // This uses your task_item.xml layout
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskTitle.setText(task.getTitle());
        holder.taskDueDate.setText("Due: " + task.getDueDate());
        holder.taskNote.setText(task.getNote());

        holder.deleteCheckBox.setOnCheckedChangeListener(null);  // Prevent recycled listener issues
        holder.deleteCheckBox.setChecked(selectedTasks.contains(task));
        holder.deleteCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedTasks.contains(task)) {
                    selectedTasks.add(task);
                }
            } else {
                selectedTasks.remove(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, taskDueDate, taskNote;
        CheckBox deleteCheckBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDueDate = itemView.findViewById(R.id.taskDueDate);
            taskNote = itemView.findViewById(R.id.taskNote);
            deleteCheckBox = itemView.findViewById(R.id.deleteCheckBox);
        }
    }

    public List<Task> getSelectedTasks() {
        return selectedTasks;
    }
}
