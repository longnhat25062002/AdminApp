package com.example.myadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myadmin.R
import com.google.firebase.database.*
import data.ListKhamPha

class ListKhamPhaAdapter(
    private val khamPhaList: List<ListKhamPha>,
    private val onEdit: (ListKhamPha) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<ListKhamPhaAdapter.KhamPhaViewHolder>() {

    private val danhMucList = mutableMapOf<Int, String>() // Lưu trữ tên danh mục
    private val baiTapKhamPhaList = mutableMapOf<Int, String>() // Lưu trữ tên bài tập

    init {
        loadDanhMuc()
        loadBaiTapKhamPha()
    }

    inner class KhamPhaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvKhamPhaName)
        val tvid: TextView = view.findViewById(R.id.tvid)
        val tvbaitap : TextView = view.findViewById(R.id.tvBaiTapKhamPha)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KhamPhaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_listkhampha, parent, false)
        return KhamPhaViewHolder(view)
    }

    override fun onBindViewHolder(holder: KhamPhaViewHolder, position: Int) {
        val khamPha = khamPhaList[position]

        // Lấy tên danh mục từ danhMucList
        val danhMucTen = danhMucList[khamPha.ID] ?: "Không có tên danh mục" // Nếu không tìm thấy tên, hiển thị mặc định
        holder.tvName.text = khamPha.Ten

        // Chuyển các ID trong ListBaiTapKhamPha thành tên bài tập
        val baiTapTenList = khamPha.ListBaiTapKhamPha?.mapNotNull { id ->
            baiTapKhamPhaList[id] // Nếu không có tên bài tập, sẽ trả về null
        }
        val baiTapTen = baiTapTenList?.joinToString(", ") // Nối tên các bài tập
        holder.tvbaitap.text = "Danh sách bài tập : " + baiTapTen

        holder.tvid.text = "Danh mục : " +  danhMucTen

        holder.btnEdit.setOnClickListener { onEdit(khamPha) }
        holder.btnDelete.setOnClickListener { onDelete(khamPha.IDKhamPha) }
    }

    override fun getItemCount(): Int = khamPhaList.size

    // Tải danh mục từ Firebase
    private fun loadDanhMuc() {
        val danhMucRef = FirebaseDatabase.getInstance().getReference("DanhMucKhamPha")
        danhMucRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                danhMucList.clear()
                for (child in snapshot.children) {
                    val id = child.child("ID").getValue(Int::class.java)
                    val name = child.child("Ten").getValue(String::class.java)
                    if (id != null && name != null) {
                        danhMucList[id] = name
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Tải bài tập khám phá từ Firebase
    private fun loadBaiTapKhamPha() {
        val baiTapRef = FirebaseDatabase.getInstance().getReference("ListBaiTapKhamPha")
        baiTapRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                baiTapKhamPhaList.clear()
                for (child in snapshot.children) {
                    val id = child.child("ID").getValue(Int::class.java)
                    val name = child.child("TenBaiTap").getValue(String::class.java)
                    if (id != null && name != null) {
                        baiTapKhamPhaList[id] = name
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
