package com.example.myadmin.adapater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myadmin.R
import data.ExciseWarmup

class ExerciseWarmupAdapter(
    private var exercises: MutableList<ExciseWarmup>,  // Use var to modify the list
    private val onEdit: (ExciseWarmup) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<ExerciseWarmupAdapter.ExerciseViewHolder>() {

    // Method to update the exercise list
    fun updateData(newExercises: List<ExciseWarmup>) {
        exercises.clear()
        exercises.addAll(newExercises)
        notifyDataSetChanged()  // Refresh the RecyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_warmup_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.bind(exercise)
        holder.editButton.setOnClickListener { onEdit(exercise) }
        holder.deleteButton.setOnClickListener { onDelete(exercise.ID) }
    }

    override fun getItemCount(): Int = exercises.size

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.txtExerciseName)
        private val timeTextView: TextView = view.findViewById(R.id.txtExerciseTime)
        private val videoTextView: TextView = view.findViewById(R.id.txtExerciseVideo)

        val editButton: View = view.findViewById(R.id.btnEditExercise)
        val deleteButton: View = view.findViewById(R.id.btnDeleteExercise)

        fun bind(exercise: ExciseWarmup) {
            nameTextView.text = "Tên bài tập : " + exercise.TenBaiTap
            timeTextView.text = "Thời gian : " + exercise.ThoiGian.toString()
            videoTextView.text = "Video : " + exercise.Video
        }
    }
}
