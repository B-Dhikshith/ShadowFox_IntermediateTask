package com.example.prioritytodoapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prioritytodoapp.adapter.TaskAdapter
import com.example.prioritytodoapp.model.Priority
import com.example.prioritytodoapp.model.Task
import com.example.prioritytodoapp.viewmodel.TaskViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerViewTasks = findViewById(R.id.recyclerViewTasks)
        fabAddTask = findViewById(R.id.fabAddTask)

        setupRecyclerView()
        observeTasks()

        fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskChecked = { task, isChecked ->
                // Post update to avoid RecyclerView layout crash
                recyclerViewTasks.post {
                    taskViewModel.updateTask(task.copy(isCompleted = isChecked))
                }
            },
            onTaskClicked = { task ->
                showEditTaskDialog(task)
            }
        )

        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewTasks.adapter = taskAdapter
    }


    private fun observeTasks() {
        taskViewModel.tasks.observe(this) { tasks ->
            taskAdapter.submitList(tasks)
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_task, null)
        val etTaskTitle = dialogView.findViewById<EditText>(R.id.etTaskTitle)
        val spinnerPriority = dialogView.findViewById<Spinner>(R.id.spinnerPriority)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveTask)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteTask)

        btnDelete.visibility = View.GONE

        spinnerPriority.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            Priority.entries.map { it.name } // Replace with enumValues<Priority>().map { it.name } if needed
        )

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add New Task")
            .setView(dialogView)
            .create()

        btnSave.setOnClickListener {
            val title = etTaskTitle.text.toString().trim()
            if (title.isNotEmpty()) {
                val priority = Priority.valueOf(spinnerPriority.selectedItem.toString())
                val newTask = Task(title = title, priority = priority)
                taskViewModel.addTask(newTask)
                dialog.dismiss()
            } else {
                etTaskTitle.error = "Task title cannot be empty"
            }
        }

        dialog.show()
    }

    private fun showEditTaskDialog(task: Task) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_task, null)
        val etTaskTitle = dialogView.findViewById<EditText>(R.id.etTaskTitle)
        val spinnerPriority = dialogView.findViewById<Spinner>(R.id.spinnerPriority)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveTask)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteTask)

        etTaskTitle.setText(task.title)

        spinnerPriority.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            Priority.entries.map { it.name }
        )

        val priorityIndex = Priority.entries.indexOf(task.priority)
        if (priorityIndex != -1) {
            spinnerPriority.setSelection(priorityIndex)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(dialogView)
            .create()
        btnSave.setOnClickListener {
            val title = etTaskTitle.text.toString().trim()
            if (title.isNotEmpty()) {
                val priority = Priority.valueOf(spinnerPriority.selectedItem.toString())
                val updatedTask = task.copy(title = title, priority = priority)
                taskViewModel.updateTask(updatedTask)
                dialog.dismiss()
            } else {
                etTaskTitle.error = "Task title cannot be empty"
            }
        }

        btnDelete.setOnClickListener {
            taskViewModel.deleteTask(task)
            dialog.dismiss()
        }

        dialog.show()
    }
}