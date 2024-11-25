package com.example.adminpt.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myadmin.ExciseWarmup
import com.example.myadmin.ExerciseWarmupAdapter
import com.example.myadmin.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*

class WarmupFragment : Fragment(R.layout.fragment_warmup) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseAdapter: ExerciseWarmupAdapter
    private lateinit var database: DatabaseReference
    private val warmupExercises = mutableListOf<ExciseWarmup>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewStretching)
        val searchView = view.findViewById<SearchView>(R.id.searchView)
        database = FirebaseDatabase.getInstance().getReference("BaiTap/KhoiDong")

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        exerciseAdapter = ExerciseWarmupAdapter(warmupExercises, this::editExercise, this::deleteExercise)
        recyclerView.adapter = exerciseAdapter

        // Load exercises from Firebase
        loadExercises()

        // Add button click
        view.findViewById<Button>(R.id.btnAddStretchingExercise).setOnClickListener {
            showAddEditDialog(null)
        }

        // Setup search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterExercises(newText)
                return true
            }
        })
    }

    private fun loadExercises() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                warmupExercises.clear()
                for (child in snapshot.children) {
                    val exercise = child.getValue(ExciseWarmup::class.java)
                    exercise?.let { warmupExercises.add(it) }
                }
                exerciseAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Log hoặc thông báo lỗi
            }
        })
    }

    private fun filterExercises(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            warmupExercises
        } else {
            warmupExercises.filter { it.TenBaiTap.contains(query, ignoreCase = true) }
        }
        exerciseAdapter.updateData(filteredList)
    }

    private fun showAddEditDialog(exercise: ExciseWarmup?) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_editwamup, null)

        // Initialize EditTexts and Spinner
        val edtName = dialogView.findViewById<EditText>(R.id.etExerciseName)
        val spPart = dialogView.findViewById<Spinner>(R.id.spExercisePart)
        val edtTime = dialogView.findViewById<EditText>(R.id.etExerciseTime)

        val parts = listOf("Ngực", "Lưng", "Chân", "Tay", "Bụng")
        val partAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, parts)
        partAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPart.adapter = partAdapter

        // Pre-fill fields if editing
        exercise?.let {
            edtName.setText(it.TenBaiTap)
            spPart.setSelection(parts.indexOf(it.BoPhan))
            edtTime.setText(it.ThoiGian.toString())
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (exercise == null) "Thêm bài tập khởi động" else "Chỉnh sửa bài tập khởi động")
            .setView(dialogView)
            .setNegativeButton("Hủy", null)
            .create()

        dialogView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = edtName.text.toString().trim()
            val part = spPart.selectedItem.toString()
            val time = edtTime.text.toString().trim().toIntOrNull()

            if (name.isEmpty()) {
                edtName.error = "Vui lòng nhập tên bài tập!"
                return@setOnClickListener
            }
            if (time == null) {
                edtTime.error = "Vui lòng nhập thời gian hợp lệ!"
                return@setOnClickListener
            }

            if (exercise == null) {
                val newId = database.push().key ?: ""
                val newExercise = ExciseWarmup(
                    ID = newId.hashCode(),
                    TenBaiTap = name,
                    BoPhan = part,
                    ThoiGian = time,
                    Video = "" // Optional
                )
                database.child(newId).setValue(newExercise)
            } else {
                val updatedExercise = exercise.copy(
                    TenBaiTap = name,
                    BoPhan = part,
                    ThoiGian = time
                )
                database.child(exercise.ID.toString()).setValue(updatedExercise)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun editExercise(exercise: ExciseWarmup) {
        showAddEditDialog(exercise)
    }

    private fun deleteExercise(id: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Xóa bài tập khởi động")
            .setMessage("Bạn có chắc chắn muốn xóa bài tập này không?")
            .setPositiveButton("Xóa") { _, _ ->
                database.child(id.toString()).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val position = warmupExercises.indexOfFirst { it.ID == id }
                        if (position >= 0) {
                            warmupExercises.removeAt(position)
                            exerciseAdapter.notifyItemRemoved(position)
                        }
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
