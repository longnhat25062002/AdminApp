package com.example.myadmin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myadmin.adapater.ListBaiTapKhamPhaAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import data.ListBaiTapKhamPha

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListBaiTapKhamPhaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListBaiTapKhamPhaFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: ListBaiTapKhamPhaAdapter
    private lateinit var database: DatabaseReference
    private val khampha = mutableListOf<ListBaiTapKhamPha>()
    private var filteredkhampha = mutableListOf<ListBaiTapKhamPha>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_bai_tap_kham_pha, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
        database = FirebaseDatabase.getInstance().getReference("ListBaiTapKhamPha")

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        categoryAdapter = ListBaiTapKhamPhaAdapter(filteredkhampha, this::editkhampha, this::deletekhampha)
        recyclerView.adapter = categoryAdapter

        // Load categories from Firebase
        loadkhampha()

        // Add button click
        view.findViewById<Button>(R.id.btnAddkhampha).setOnClickListener {
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
    private fun loadkhampha() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                khampha.clear()
                for (child in snapshot.children) {
                    val category = child.getValue(ListBaiTapKhamPha::class.java)
                    category?.let { khampha.add(it) }
                }
                filterCategories("") // Apply no filter initially
            }

            override fun onCancelled(error: DatabaseError) {
                // Log hoặc thông báo lỗi
            }
        })
    }

    private fun filterCategories(query: String?) {
        filteredkhampha.clear()
        if (query.isNullOrEmpty()) {
            filteredkhampha.addAll(khampha)  // No filter
        } else {
            for (kp in khampha) {
                if (kp.TenBaiTap.contains(query, ignoreCase = true)) {
                    filteredkhampha.add(kp)  // Add matching categories
                }
            }
        }
        categoryAdapter.notifyDataSetChanged()
    }
    private fun showAddEditDialog(khamPha: ListBaiTapKhamPha?) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_khampha, null)

        // Initialize EditTexts
        val edtName = dialogView.findViewById<EditText>(R.id.khamphaName)
        val edtsorep = dialogView.findViewById<EditText>(R.id.sorep)
        val edtthoigian = dialogView.findViewById<EditText>(R.id.thoigian)
        val edtvideo = dialogView.findViewById<EditText>(R.id.video)
        edtsorep.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                edtthoigian.isEnabled = s.isNullOrEmpty() // Disable "Thời gian" if "Số rep" is not empty
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Listener for "Thời gian" field
        edtthoigian.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                edtsorep.isEnabled = s.isNullOrEmpty() // Disable "Số rep" if "Thời gian" is not empty
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        // Pre-fill fields if editing
        khamPha?.let {
            edtName.setText(it.TenBaiTap)
            edtsorep.setText(it.SoRep)
            edtthoigian.setText(it.ThoiGian)
            edtvideo.setText(it.Video)
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (khamPha == null) "Thêm bài tập khám phá" else "Chỉnh sửa bài tập khám phá")
            .setView(dialogView)
            .setNegativeButton("Hủy", null)
            .create()

        dialogView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = edtName.text.toString().trim()
            val thoigian = edtthoigian.text.toString().trim()
            val sorep = edtsorep.text.toString().trim()
            val video = edtvideo.text.toString().trim()

            if (name.isEmpty()) {
                edtName.error = "Vui lòng nhập tên danh mục!"
                return@setOnClickListener
            }

            if (khamPha == null) {
                val newId = database.push().key.hashCode()
                val newCategory = ListBaiTapKhamPha(
                    ID = newId,
                    TenBaiTap = name,
                    SoRep = sorep,
                    ThoiGian = thoigian,
                    Video = video
                )
                database.child(newCategory.ID.toString()).setValue(newCategory)
            } else {
                val updatedCategory = khamPha.copy(
                    TenBaiTap = name,
                    SoRep = sorep,
                    ThoiGian = thoigian,
                    Video = video
                )
                database.child(khamPha.ID.toString()).setValue(updatedCategory)
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun editkhampha(khamPha: ListBaiTapKhamPha) {
        showAddEditDialog(khamPha)
    }

    private fun deletekhampha(id: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Xóa bài tập")
            .setMessage("Bạn có chắc chắn muốn xóa bài tập này không?")
            .setPositiveButton("Xóa") { _, _ ->
                database.child(id.toString()).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val position = khampha.indexOfFirst { it.ID == id }
                        if (position >= 0) {
                            khampha.removeAt(position)
                            categoryAdapter.notifyItemRemoved(position)
                        }
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

}