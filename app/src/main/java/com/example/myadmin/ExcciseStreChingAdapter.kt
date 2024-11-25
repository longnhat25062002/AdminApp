package com.example.myadmin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.Exercise
import com.example.myadmin.R

class ExcciseStreChingAdapter(
    private val exercises: MutableList<Exercise>,
    private val onEdit: (Exercise) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<ExcciseStreChingAdapter.ExerciseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stretching_excise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.nameTextView.text = exercise.TenBaiTap
        holder.partTextView.text = exercise.BoPhan
        holder.levelTextView.text = exercise.MucDo
        holder.repsTextView.text = "Số Rep: ${exercise.SoRep}"
        holder.editButton.setOnClickListener { onEdit(exercise) }
        holder.deleteButton.setOnClickListener { onDelete(exercise.ID) }
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.tvExerciseName)
        val partTextView: TextView = view.findViewById(R.id.tvExercisePart)
        val levelTextView: TextView = view.findViewById(R.id.tvExerciseLevel)
        val repsTextView: TextView = view.findViewById(R.id.tvExerciseReps)
        val editButton: Button = view.findViewById(R.id.btnEdit)
        val deleteButton: Button = view.findViewById(R.id.btnDelete)
    }
}
