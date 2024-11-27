package com.example.myadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myadmin.adapter.ListKhamPhaAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import data.Category
import data.ListBaiTapKhamPha
import data.ListKhamPha


class ListKhamPhaFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var khamPhaAdapter: ListKhamPhaAdapter
    private lateinit var database: DatabaseReference
    private val khamPhaList = mutableListOf<ListKhamPha>()
    private val filteredKhamPhaList = mutableListOf<ListKhamPha>()

    private val danhMucList = mutableListOf<Category>()
    private val baiTapList = mutableListOf<ListBaiTapKhamPha>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_kham_pha, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
        database = FirebaseDatabase.getInstance().getReference("BaiTapKhamPha")

        recyclerView.layoutManager = LinearLayoutManager(context)
        khamPhaAdapter = ListKhamPhaAdapter(filteredKhamPhaList, this::editKhamPha, this::deleteKhamPha)
        recyclerView.adapter = khamPhaAdapter

        loadKhamPha()

        // Set up the "Add" button
        view.findViewById<Button>(R.id.btnAddKhamPha).setOnClickListener {
            showAddEditDialog(null)
        }

        // Set up search functionality
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterKhamPha(newText)
                return true
            }
        })

        // Load DanhMucKhamPha and BaiTapKhamPha
        loadDanhMucKhamPha()
        loadBaiTapKhamPha()
    }

    private fun loadKhamPha() {
        // Load list from Firebase Database
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                khamPhaList.clear()
                for (child in snapshot.children) {
                    val khamPha = child.getValue(ListKhamPha::class.java)
                    khamPha?.let { khamPhaList.add(it) }
                }
                filterKhamPha("")
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun loadDanhMucKhamPha() {
        // Load DanhMucKhamPha from Firebase
        val danhMucRef = FirebaseDatabase.getInstance().getReference("DanhMucKhamPha")
        danhMucRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                danhMucList.clear()
                for (child in snapshot.children) {
                    val danhMuc = child.getValue(Category::class.java)
                    danhMuc?.let { danhMucList.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun loadBaiTapKhamPha() {
        // Load BaiTapKhamPha from Firebase
        val baiTapRef = FirebaseDatabase.getInstance().getReference("ListBaiTapKhamPha")
        baiTapRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                baiTapList.clear()
                for (child in snapshot.children) {
                    val baiTap = child.getValue(ListBaiTapKhamPha::class.java)
                    baiTap?.let { baiTapList.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun filterKhamPha(query: String?) {
        filteredKhamPhaList.clear()
        if (query.isNullOrEmpty()) {
            filteredKhamPhaList.addAll(khamPhaList)
        } else {
            for (kp in khamPhaList) {
                if (kp.Ten.contains(query, ignoreCase = true)) {
                    filteredKhamPhaList.add(kp)
                }
            }
        }
        khamPhaAdapter.notifyDataSetChanged()
    }

    private fun showAddEditDialog(khamPha: ListKhamPha?) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_listkhampha, null)
        val edtName = dialogView.findViewById<EditText>(R.id.khamphaName)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val multiSelectBaiTap = dialogView.findViewById<MultiAutoCompleteTextView>(R.id.multiSelectBaiTap)

        // Setup category spinner
        val categoryNames = danhMucList.map { it.Ten }
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        // Setup MultiAutoCompleteTextView for BaiTap
        val baiTapNames = mutableListOf<String>()
        val baiTapRef = FirebaseDatabase.getInstance().getReference("ListBaiTapKhamPha")
        baiTapRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                baiTapList.clear()
                baiTapNames.clear()

                snapshot.children.forEach {
                    val baiTap = it.getValue(ListBaiTapKhamPha::class.java)
                    if (baiTap != null) {
                        baiTapList.add(baiTap)
                        baiTapNames.add(baiTap.TenBaiTap)
                    }
                }

                // Create adapter for MultiAutoCompleteTextView
                val baiTapAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    baiTapNames
                )
                multiSelectBaiTap.setAdapter(baiTapAdapter)
                multiSelectBaiTap.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

                // If editing an existing item, pre-select previously selected BaiTap
                if (khamPha != null) {
                    val selectedBaiTapNames = baiTapList
                        .filter { it.ID in (khamPha.ListBaiTapKhamPha ?: listOf()) }
                        .map { it.TenBaiTap }
                    multiSelectBaiTap.setText(selectedBaiTapNames.joinToString(", "))
                    edtName.setText( khamPha.Ten)
                    categorySpinner.setSelection(danhMucList.indexOfFirst { it.ID == khamPha.ID })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (khamPha == null) "Thêm Khám Phá" else "Chỉnh sửa Khám Phá")
            .setView(dialogView)
            .setPositiveButton("Lưu") { _, _ ->
                val name = edtName.text.toString().trim()
                val selectedCategoryName = categorySpinner.selectedItem.toString()
                val selectedCategoryId = danhMucList.first { it.Ten == selectedCategoryName }.ID

                // Parse selected BaiTap
                val selectedBaiTapNames = multiSelectBaiTap.text.toString()
                    .split(",")
                    .map { it.trim() }

                val selectedBaiTapIds = baiTapList
                    .filter { selectedBaiTapNames.contains(it.TenBaiTap) }
                    .map { it.ID }

                if (name.isEmpty()) {
                    edtName.error = "Vui lòng nhập tên!"
                    return@setPositiveButton
                }

                if (khamPha == null) {
                    val newId = database.push().key.hashCode()
                    val newKhamPha = ListKhamPha(

                        IDKhamPha = newId.toInt(),
                        Ten = name,
                        ListBaiTapKhamPha = selectedBaiTapIds,
                        ID = selectedCategoryId // Gán ID danh mục đã chọn
                    )
                    database.child(newId.toString()).setValue(newKhamPha)
                } else {
                    khamPha.Ten = name
                    khamPha.ListBaiTapKhamPha = selectedBaiTapIds
                    khamPha.ID = selectedCategoryId // Cập nhật ID danh mục đã chọn
                    database.child(khamPha.IDKhamPha.toString()).setValue(khamPha)
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }




    private fun editKhamPha(khamPha: ListKhamPha) {
        showAddEditDialog(khamPha)
    }

    private fun deleteKhamPha(id: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Xóa Khám Phá")
            .setMessage("Bạn có chắc chắn muốn xóa?")
            .setPositiveButton("Xóa") { _, _ ->
                database.child(id.toString()).removeValue()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
