package com.example.myadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import data.Exercise
import com.example.myadmin.adapater.ExerciseAdapter
import com.example.myadmin.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*

class MainWorkoutFragment : Fragment(R.layout.fragment_main_workout) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var database: DatabaseReference
    private val exercises = mutableListOf<Exercise>()
    private var filteredExercises = mutableListOf<Exercise>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etSearchExercise = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.etSearchExercise)
        val btnAddExercise = view.findViewById<Button>(R.id.btnAddExercise)
        recyclerView = view.findViewById(R.id.recyclerView)

        // Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("BaiTap/BaiTapChinh")

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        exerciseAdapter = ExerciseAdapter(filteredExercises, this::editExercise, this::deleteExercise)
        recyclerView.adapter = exerciseAdapter

        // Load exercises from Firebase
        loadExercises()

        // Handle Add button click
        btnAddExercise.setOnClickListener {
            showAddEditDialog(null) // Open dialog to add a new exercise
        }
        etSearchExercise.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Return false because we don't want to submit the query
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter exercises based on search text
                filterExercises(newText)
                return true
            }
        })

    }

    private fun loadExercises() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                exercises.clear()
                for (child in snapshot.children) {
                    val exercise = child.getValue(Exercise::class.java)
                    exercise?.let { exercises.add(it) }
                }
                filterExercises("") // Load full list initially
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun filterExercises(query: String?) {
        filteredExercises.clear()
        if (query.isNullOrEmpty()) {
            filteredExercises.addAll(exercises)  // No filter
        } else {
            for (exercise in exercises) {
                if (exercise.TenBaiTap.contains(query, ignoreCase = true)) {
                    filteredExercises.add(exercise)  // Add matching exercises
                }
            }
        }
        exerciseAdapter.notifyDataSetChanged()  // Refresh the RecyclerView
    }

    private fun showAddEditDialog(exercise: Exercise?) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit, null)

        // Initialize Spinner fields
        val spPart = dialogView.findViewById<Spinner>(R.id.spExercisePart)
        val spLevel = dialogView.findViewById<Spinner>(R.id.spExerciseLevel)

        val parts = listOf("Ngực", "Vai và Lưng", "Chân", "Cánh Tay", "Bụng")
        val levels = listOf("Dễ", "Trung bình", "Khó")

        // Use ArrayAdapter for Spinner parts and levels
        val partAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, parts)
        partAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPart.adapter = partAdapter

        val levelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, levels)
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLevel.adapter = levelAdapter

        // Pre-fill data if editing an existing exercise
        exercise?.let {
            val partPosition = parts.indexOf(it.BoPhan)
            val levelPosition = levels.indexOf(it.MucDo)

            spPart.setSelection(partPosition)
            spLevel.setSelection(levelPosition)
        }
        // Nếu exercise != null, nghĩa là chỉnh sửa, tải dữ liệu từ Firebase
        if (exercise != null) {
            database.child(exercise.ID.toString()).get().addOnSuccessListener { dataSnapshot ->
                val currentExercise = dataSnapshot.getValue(Exercise::class.java)
                if (currentExercise != null) {
                    val name = dialogView.findViewById<EditText>(R.id.etExerciseName)
                    val reps = dialogView.findViewById<EditText>(R.id.etExerciseReps)
                    val video = dialogView.findViewById<EditText>(R.id.etExerciseVideo)
                    // Điền dữ liệu hiện có vào các trường
                    name.setText(currentExercise.TenBaiTap)
                    reps.setText(currentExercise.SoRep.toString())
                    video.setText(currentExercise.Video)

                    // Đặt spinner về giá trị hiện tại
                    spPart.setSelection(getIndex(spPart, currentExercise.BoPhan))
                    spLevel.setSelection(getIndex(spLevel, currentExercise.MucDo))
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show()
            }
        }

        // Show dialog
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (exercise == null) "Thêm bài tập" else "Chỉnh sửa bài tập")
            .setView(dialogView)
            .setNegativeButton("Hủy", null)
            .create()

        dialogView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = dialogView.findViewById<EditText>(R.id.etExerciseName).text.toString().trim()
            val part = spPart.selectedItem.toString()
            val level = spLevel.selectedItem.toString()
            val reps = dialogView.findViewById<EditText>(R.id.etExerciseReps).text.toString().trim().toIntOrNull()
            val video = dialogView.findViewById<EditText>(R.id.etExerciseVideo).text.toString().trim()
            if (name.isNotEmpty() && part.isNotEmpty() && level.isNotEmpty() && reps != null) {
                if (exercise == null) {
                    // Create a new exercise
                    val newExercise = Exercise(
                        ID = database.push().key.hashCode(),
                        TenBaiTap = name,
                        BoPhan = part,
                        MucDo = level,
                        SoRep = reps,
                        Video = video
                    )
                    database.child(newExercise.ID.toString()).setValue(newExercise)
                } else {

                    // Update an existing exercise
                    exercise.TenBaiTap = name
                    exercise.BoPhan = part
                    exercise.MucDo = level
                    exercise.SoRep = reps
                    exercise.Video = video
                    database.child(exercise.ID.toString()).setValue(exercise)
                }
                dialog.dismiss()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Lỗi")
                    .setMessage("Vui lòng điền đầy đủ thông tin!")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        dialog.show()
    }


    fun getIndex(spinner: Spinner, value: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == value) {
                return i
            }
        }
        return 0
    }
    private fun editExercise(exercise: Exercise) {
        showAddEditDialog(exercise)
    }

    private fun deleteExercise(id: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Xóa bài tập")
            .setMessage("Bạn có chắc chắn muốn xóa bài tập này không?")
            .setPositiveButton("Xóa") { _, _ ->
                database.child(id.toString()).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val position = exercises.indexOfFirst { it.ID == id }
                        if (position >= 0) {
                            exercises.removeAt(position)
                            filterExercises("") // Refresh filtered list
                        }
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}

