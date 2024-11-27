package com.example.adminpt.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myadmin.adapater.ExcciseStreChingAdapter
import data.ExciseStreching
import com.example.myadmin.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*

class StretchingFragment : Fragment(R.layout.fragment_stretching) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseAdapter: ExcciseStreChingAdapter
    private lateinit var database: DatabaseReference
    private val stretchingExercises = mutableListOf<ExciseStreching>()
    private val filteredExercises = mutableListOf<ExciseStreching>()  // List to hold filtered results

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewStretching)
        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
        database = FirebaseDatabase.getInstance().getReference("BaiTap/GianCo")

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        exerciseAdapter = ExcciseStreChingAdapter(filteredExercises, this::editExercise, this::deleteExercise)
        recyclerView.adapter = exerciseAdapter

        // Load exercises from Firebase
        loadExercises()

        // Add button click
        view.findViewById<Button>(R.id.btnAddStretchingExercise).setOnClickListener {
            showAddEditDialog(null) // Open dialog to add a new stretching exercise
        }

        // Set up search functionality
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
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
                stretchingExercises.clear()
                for (child in snapshot.children) {
                    val exercise = child.getValue(ExciseStreching::class.java)
                    exercise?.let { stretchingExercises.add(it) }
                }
                filteredExercises.clear()
                filteredExercises.addAll(stretchingExercises)
                exerciseAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun filterExercises(query: String?) {
        filteredExercises.clear()
        if (query.isNullOrEmpty()) {
            filteredExercises.addAll(stretchingExercises)  // No filter
        } else {
            for (exercise in stretchingExercises) {
                if (exercise.TenBaiTap.contains(query, ignoreCase = true)) {
                    filteredExercises.add(exercise)  // Add matching exercises
                }
            }
        }
        exerciseAdapter.notifyDataSetChanged()  // Refresh the RecyclerView
    }

    private fun showAddEditDialog(exercise: ExciseStreching?) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edittretching, null)

        // Initialize EditTexts and Spinners
        val edtName = dialogView.findViewById<EditText>(R.id.etExerciseName)
        val edttime = dialogView.findViewById<EditText>(R.id.etExerciseTime)
        val edtvideo = dialogView.findViewById<EditText>(R.id.etExerciseVideo)


        // Show MaterialAlertDialog
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (exercise == null) "Thêm bài tập khám phá" else "Chỉnh sửa bài tập khám phá")
            .setView(dialogView)
            .setNegativeButton("Hủy", null)
            .create()
        if (exercise != null) {
            database.child(exercise.ID.toString()).get().addOnSuccessListener { dataSnapshot ->
                val currentExercise = dataSnapshot.getValue(ExciseStreching::class.java)
                if (currentExercise != null) {
                    val name = dialogView.findViewById<EditText>(R.id.etExerciseName)
                    val reps = dialogView.findViewById<EditText>(R.id.etExerciseTime)
                    val video = dialogView.findViewById<EditText>(R.id.etExerciseVideo)
                    // Điền dữ liệu hiện có vào các trường
                    name.setText(currentExercise.TenBaiTap)
                    reps.setText(currentExercise.ThoiGian.toString())
                    video.setText(currentExercise.Video)

                }
            }.addOnFailureListener {
                Toast.makeText(context, "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show()
            }
        }
        // Save button logic
        dialogView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = edtName.text.toString().trim()
            val time = edttime.text.toString().trim().toIntOrNull()
            val video = edtvideo.text.toString().trim()
            if (name.isNotEmpty()&& video.isNotEmpty() && time != null) {
                if (exercise == null) {
                    // Add new exercise
                    val newExercise = ExciseStreching(
                        ID = database.push().key.hashCode(),
                        TenBaiTap = name,
                        ThoiGian = time,
                        Video = video
                    )
                    database.child(newExercise.ID.toString()).setValue(newExercise)
                } else {
                    // Update existing exercise
                    val updatedExercise = exercise.copy(
                        TenBaiTap = name,
                        ThoiGian = time,
                        Video = video
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

    private fun editExercise(exercise: ExciseStreching) {
        showAddEditDialog(exercise)
    }

    private fun deleteExercise(id: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Xóa bài tập giãn cơ")
            .setMessage("Bạn có chắc chắn muốn xóa bài tập này không?")
            .setPositiveButton("Xóa") { _, _ ->
                database.child(id.toString()).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val position = stretchingExercises.indexOfFirst { it.ID == id }
                        if (position >= 0) {
                            stretchingExercises.removeAt(position)
                            exerciseAdapter.notifyItemRemoved(position)
                        }
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}

