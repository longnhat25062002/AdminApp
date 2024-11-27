package com.example.myadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myadmin.adapater.CategoryAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import data.Category

class CategoryFragment : Fragment(R.layout.fragment_category) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var database: DatabaseReference
    private val categories = mutableListOf<Category>()
    private var filteredCategories = mutableListOf<Category>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewCategories)
        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchViewCategory)
        database = FirebaseDatabase.getInstance().getReference("DanhMucKhamPha")

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        categoryAdapter = CategoryAdapter(filteredCategories, this::editCategory, this::deleteCategory)
        recyclerView.adapter = categoryAdapter

        // Load categories from Firebase
        loadCategories()

        // Add button click
        view.findViewById<Button>(R.id.btnAddCategory).setOnClickListener {
            showAddEditDialog(null)
        }

        // Setup search functionality
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCategories(newText)
                return true
            }
        })
    }

    private fun loadCategories() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categories.clear()
                for (child in snapshot.children) {
                    val category = child.getValue(Category::class.java)
                    category?.let { categories.add(it) }
                }
                filterCategories("") // Apply no filter initially
            }

            override fun onCancelled(error: DatabaseError) {
                // Log hoặc thông báo lỗi
            }
        })
    }

    private fun filterCategories(query: String?) {
        filteredCategories.clear()
        if (query.isNullOrEmpty()) {
            filteredCategories.addAll(categories)  // No filter
        } else {
            for (category in categories) {
                if (category.Ten.contains(query, ignoreCase = true)) {
                    filteredCategories.add(category)  // Add matching categories
                }
            }
        }
        categoryAdapter.notifyDataSetChanged()
    }

    private fun showAddEditDialog(category: Category?) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_category, null)

        // Initialize EditTexts
        val edtName = dialogView.findViewById<EditText>(R.id.CategoryName)

        // Pre-fill fields if editing
        category?.let {
            edtName.setText(it.Ten)
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (category == null) "Thêm danh mục" else "Chỉnh sửa danh mục")
            .setView(dialogView)
            .setNegativeButton("Hủy", null)
            .create()

        dialogView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = edtName.text.toString().trim()

            if (name.isEmpty()) {
                edtName.error = "Vui lòng nhập tên danh mục!"
                return@setOnClickListener
            }

            if (category == null) {
                val newId = database.push().key.hashCode()
                val newCategory = Category(
                    ID = newId,
                    Ten = name
                )
                database.child(newCategory.ID.toString()).setValue(newCategory)
            } else {
                val updatedCategory = category.copy(
                    Ten = name
                )
                database.child(category.ID.toString()).setValue(updatedCategory)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun editCategory(category: Category) {
        showAddEditDialog(category)
    }

    private fun deleteCategory(id: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Xóa danh mục")
            .setMessage("Bạn có chắc chắn muốn xóa danh mục này không?")
            .setPositiveButton("Xóa") { _, _ ->
                database.child(id.toString()).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val position = categories.indexOfFirst { it.ID == id }
                        if (position >= 0) {
                            categories.removeAt(position)
                            categoryAdapter.notifyItemRemoved(position)
                        }
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
