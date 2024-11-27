package com.example.myadmin.adapater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.myadmin.R
import data.ExciseStreching

class ExcciseStreChingAdapter(
    private val exercises: MutableList<ExciseStreching>,
    private val onEdit: (ExciseStreching) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<ExcciseStreChingAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stretching_excise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.nameTextView.text = "Tên bài tập : " + exercise.TenBaiTap
        holder.repsTextView.text = "Thời gian : ${exercise.ThoiGian}"
        holder.ThoiGianTextView.text = "Video : " + exercise.Video
        holder.editButton.setOnClickListener { onEdit(exercise) }
        holder.deleteButton.setOnClickListener { onDelete(exercise.ID) }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.txtExerciseName)
        val repsTextView: TextView = view.findViewById(R.id.txtExerciseTime)
        val ThoiGianTextView: TextView = view.findViewById(R.id.txtExerciseVideo)
        val editButton: Button = view.findViewById(R.id.btnEditExercise)
        val deleteButton: Button = view.findViewById(R.id.btnDeleteExercise)
    }
}
