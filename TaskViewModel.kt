// TaskViewModel.kt
package com.example.prioritytodoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.prioritytodoapp.model.Priority
import com.example.prioritytodoapp.model.Task

class TaskViewModel : ViewModel() {

    private val _tasks = MutableLiveData<MutableList<Task>>(mutableListOf())
    val tasks: LiveData<MutableList<Task>> = _tasks

    // Counter for simple task ID generation (in a real app, use UUID or database ID)
    private var nextTaskId = 0

    init {
        // Add some dummy data for demonstration
        addTask(Task(title = "Buy groceries", priority = Priority.HIGH))
        addTask(Task(title = "Call mom", priority = Priority.MEDIUM))
        addTask(Task(title = "Read a book", priority = Priority.LOW))
        addTask(Task(title = "Finish project report", priority = Priority.HIGH, isCompleted = true))
    }

    fun addTask(task: Task) {
        val currentList = _tasks.value ?: mutableListOf()
        currentList.add(task.copy(id = (nextTaskId++).toString())) // Assign unique ID
        _tasks.value = sortTasks(currentList)
    }

    fun updateTask(updatedTask: Task) {
        val currentList = _tasks.value ?: mutableListOf()
        val index = currentList.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            currentList[index] = updatedTask
            _tasks.value = sortTasks(currentList)
        }
    }

    fun deleteTask(task: Task) {
        val currentList = _tasks.value ?: mutableListOf()
        currentList.remove(task)
        _tasks.value = sortTasks(currentList)
    }

    private fun sortTasks(taskList: MutableList<Task>): MutableList<Task> {
        // Sort by completion status (incomplete first) then by priority (High, Medium, Low)
        return taskList.sortedWith(compareBy<Task> { it.isCompleted }
            .thenByDescending {
                when (it.priority) {
                    Priority.HIGH -> 3
                    Priority.MEDIUM -> 2
                    Priority.LOW -> 1
                }
            })
            .toMutableList()
    }
}