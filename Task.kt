// Task.kt
package com.example.prioritytodoapp.model

data class Task(
    val id: String = java.util.UUID.randomUUID().toString(), // Unique ID for each task
    var title: String,
    var priority: Priority,
    var isCompleted: Boolean = false
)

enum class Priority {
    HIGH, MEDIUM, LOW
}