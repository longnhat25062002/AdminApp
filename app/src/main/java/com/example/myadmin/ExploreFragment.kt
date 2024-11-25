package com.example.myadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.Exercise

import com.example.myadmin.R
import com.example.myadmin.ExerciseAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*

class ExploreFragment : Fragment(R.layout.fragment_explore) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var database: DatabaseReference
    private val exploreExercises = mutableListOf<Exercise>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewExplore)
        database = FirebaseDatabase.getInstance().getReference("BaiTap/BaiTapKhamPha")

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        exerciseAdapter = ExerciseAdapter(exploreExercises, this::editExercise, this::deleteExercise)
        recyclerView.adapter = exerciseAdapter



        // Add button click
        view.findViewById<Button>(R.id.btnAddExploreExercise).setOnClickListener {
            showAddEditDialog(null) // Open dialog to add a new explore exercise
        }
    }

    private fun showAddEditDialog(exercise: Exercise?) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit, null)

        // Initialize EditTexts and Spinners
        val edtName = dialogView.findViewById<EditText>(R.id.etExerciseName)
        val spPart = dialogView.findViewById<Spinner>(R.id.spExercisePart)
        val spLevel = dialogView.findViewById<Spinner>(R.id.spExerciseLevel)
        val edtReps = dialogView.findViewById<EditText>(R.id.etExerciseReps)

        // Pre-fill fields if editing
        exercise?.let {
            edtName.setText(it.TenBaiTap)
            val parts = listOf("Ngực", "Lưng", "Chân", "Tay", "Bụng")
            val levels = listOf("Dễ", "Trung bình", "Khó")

            // Setup adapters for Spinners
            val partAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, parts)
            partAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spPart.adapter = partAdapter

            val levelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, levels)
            levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spLevel.adapter = levelAdapter

            // Set pre-selected values
            spPart.setSelection(parts.indexOf(it.BoPhan))
            spLevel.setSelection(levels.indexOf(it.MucDo))
            edtReps.setText(it.SoRep.toString())
        }

        // Show MaterialAlertDialog
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (exercise == null) "Thêm bài tập khám phá" else "Chỉnh sửa bài tập khám phá")
            .setView(dialogView)
            .setNegativeButton("Hủy", null)
            .create()

        // Save button logic
        dialogView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = edtName.text.toString().trim()
            val part = spPart.selectedItem.toString()
            val level = spLevel.selectedItem.toString()
            val reps = edtReps.text.toString().trim().toIntOrNull()

            if (name.isNotEmpty() && part.isNotEmpty() && level.isNotEmpty() && reps != null) {
                if (exercise == null) {
                    // Add new explore exercise
                    val newExercise = Exercise(
                        ID = database.push().key.hashCode(),
                        TenBaiTap = name,
                        BoPhan = part,
                        MucDo = level,
                        SoRep = reps
                    )
                    database.child(newExercise.ID.toString()).setValue(newExercise)
                } else {
                    // Update existing explore exercise
                    val updatedExercise = exercise.copy(
                        TenBaiTap = name,
                        BoPhan = part,
                        MucDo = level,
                        SoRep = reps
                    )
                    database.child(updatedExercise.ID.toString()).setValue(updatedExercise)
                }
                dialog.dismiss()
            } else {
                // Show error if input is invalid
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Lỗi")
                    .setMessage("Vui lòng điền đầy đủ thông tin!")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        dialog.show()
    }


    private fun editExercise(exercise: Exercise) {
        showAddEditDialog(exercise)
    }

    private fun deleteExercise(id: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Xóa bài tập khám phá")
            .setMessage("Bạn có chắc chắn muốn xóa bài tập này không?")
            .setPositiveButton("Xóa") { _, _ ->
                database.child(id.toString()).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val position = exploreExercises.indexOfFirst { it.ID == id }
                        if (position >= 0) {
                            exploreExercises.removeAt(position)
                            exerciseAdapter.notifyItemRemoved(position)
                        }
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
