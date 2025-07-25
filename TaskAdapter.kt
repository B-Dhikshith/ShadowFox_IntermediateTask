package com.example.prioritytodoapp.adapter // Ensure this matches your package structure

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.prioritytodoapp.R
import com.example.prioritytodoapp.model.Priority
import com.example.prioritytodoapp.model.Task

class TaskAdapter(
    // Lambda functions (callbacks) to handle events from the ViewHolder
    private val onTaskChecked: (Task, Boolean) -> Unit, // Callback when checkbox state changes
    private val onTaskClicked: (Task) -> Unit // Callback when the whole item is clicked (for editing)
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks: List<Task> = emptyList()

    /**
     * Updates the list of tasks displayed by the adapter.
     * In a real app, consider using DiffUtil for more efficient updates.
     */
    fun submitList(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged() // Notifies RecyclerView that the data set has changed
    }

    /**
     * ViewHolder for each task item in the RecyclerView.
     * It holds references to the views within item_task.xml.
     */
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Get references to the UI elements in item_task.xml
        val checkboxComplete: CheckBox = itemView.findViewById(R.id.checkboxComplete)
        val tvTaskTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val tvTaskPriority: TextView = itemView.findViewById(R.id.tvTaskPriority)

        init {
            // Set up listener for the CheckBox
            checkboxComplete.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                // Check if position is valid to prevent crashes if item is removed
                if (position != RecyclerView.NO_POSITION) {
                    onTaskChecked(tasks[position], isChecked) // Invoke the callback
                }
            }

            // Set up listener for clicks on the entire item view
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTaskClicked(tasks[position]) // Invoke the callback
                }
            }
        }

        /**
         * Binds a Task object's data to the views in the ViewHolder.
         */
        fun bind(task: Task) {
            checkboxComplete.isChecked = task.isCompleted
            tvTaskTitle.text = task.title

            // Apply or remove strikethrough based on completion status
            if (task.isCompleted) {
                tvTaskTitle.paintFlags = tvTaskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                tvTaskTitle.paintFlags = tvTaskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            // Set priority text and background color based on Priority enum
            tvTaskPriority.text = task.priority.name // Displays HIGH, MEDIUM, or LOW
            val priorityBgResId = when (task.priority) {
                Priority.HIGH -> R.drawable.priority_high_bg
                Priority.MEDIUM -> R.drawable.priority_medium_bg
                Priority.LOW -> R.drawable.priority_low_bg
            }
            tvTaskPriority.setBackgroundResource(priorityBgResId)
        }
    }

    /**
     * Called when RecyclerView needs a new TaskViewHolder to represent an item.
     * It inflates the item_task.xml layout.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method updates the contents of the itemView to reflect the item at the given position.
     */
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position]) // Call bind method to populate views
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int = tasks.size
}