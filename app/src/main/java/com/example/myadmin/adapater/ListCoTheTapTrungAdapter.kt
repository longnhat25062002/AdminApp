package com.example.myadmin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myadmin.R
import com.google.firebase.database.*
import data.CoTheTapTrung
import data.ListKhamPha

class ListCoTheTapTrungAdapter(
    private val khamPhaLists: List<List<CoTheTapTrung>>, private val onEdit: (CoTheTapTrung,String) -> Unit,// Tổng hợp tất cả danh sách thành một list các list
) : RecyclerView.Adapter<ListCoTheTapTrungAdapter.TapTrungViewHolder>() {

    private val GianCo = mutableMapOf<Int, String>()
    private val BaiTapChinh = mutableMapOf<Int, String>()
    private val KhoiDong = mutableMapOf<Int, String>()

    init {
        loadGianCo()
        loadBaiTapChinh()
        loadKhoiDong()
    }

    inner class TapTrungViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvKhamPhaName)
        val tvid: TextView = view.findViewById(R.id.tvid)
        val tv2: TextView = view.findViewById(R.id.tv2)
        val tvbaitap: TextView = view.findViewById(R.id.tvBaiTapKhamPha)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TapTrungViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_taptrung, parent, false)
        return TapTrungViewHolder(view)
    }

    override fun onBindViewHolder(holder: TapTrungViewHolder, position: Int) {
        // Xác định danh sách nào chứa item ở vị trí này
        val totalItems = khamPhaLists.sumOf { it.size }
        var currentPos = position
        var selectedItem: CoTheTapTrung? = null

        for (list in khamPhaLists) {
            if (currentPos < list.size) {
                selectedItem = list[currentPos]
                break
            }
            currentPos -= list.size
        }
        var bophan = "";
        selectedItem?.let { khamPha ->
            // Lấy danh sách bài tập
            val baiTapTenList = khamPha.GianCo?.mapNotNull { id -> GianCo[id] }
            val baiTapTen = baiTapTenList?.joinToString(", ")

            val baiTapChinhTenList = khamPha.BaiTapChinh?.mapNotNull { id -> BaiTapChinh[id] }
            val baiTapTenChinh = baiTapChinhTenList?.joinToString(", ")

            val baiTapKhoidongTenList = khamPha.KhoiDong?.mapNotNull { id -> KhoiDong[id] }
            val baiTapTenkhoidong = baiTapKhoidongTenList?.joinToString(", ")

            when (position) {
                0 -> {
                    bophan = "Bung"
                    holder.tvName.text = "Bụng"
                }
                1 ->  {
                    bophan = "Nguc"
                    holder.tvName.text = "Ngực"
                }
                2 -> {
                    holder.tvName.text = "Cánh tay và vai"
                    bophan = "CanhTayVaVai"
                }
                3 -> {
                    bophan = "MongVaChan"
                    holder.tvName.text = "Chân và mông"
                }
                else -> bophan = "Khác" // Nếu có nhiều danh mục hơn
            }

            holder.tv2.text = "Giãn cơ: $baiTapTen"
            holder.tvid.text = "Bài tập chính: $baiTapTenChinh"
            holder.tvbaitap.text = "Khởi động: $baiTapTenkhoidong"

        }
        holder.btnEdit.setOnClickListener { onEdit(selectedItem?: CoTheTapTrung(),bophan ) }
    }

    override fun getItemCount(): Int {
        // Tổng số item từ tất cả danh sách
        return khamPhaLists.sumOf { it.size }
    }

    private fun loadKhoiDong() {
        val danhMucRef = FirebaseDatabase.getInstance().getReference("BaiTap/KhoiDong")
        danhMucRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                KhoiDong.clear()
                for (child in snapshot.children) {
                    val id = child.child("ID").getValue(Int::class.java)
                    val name = child.child("TenBaiTap").getValue(String::class.java)
                    if (id != null && name != null) {
                        KhoiDong[id] = name
                    }
                }
                notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadGianCo() {
        val danhMucRef = FirebaseDatabase.getInstance().getReference("BaiTap/GianCo")
        danhMucRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                GianCo.clear()
                for (child in snapshot.children) {
                    val id = child.child("ID").getValue(Int::class.java)
                    val name = child.child("TenBaiTap").getValue(String::class.java)
                    if (id != null && name != null) {
                        GianCo[id] = name
                    }
                }
                notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadBaiTapChinh() {
        val danhMucRef = FirebaseDatabase.getInstance().getReference("BaiTap/BaiTapChinh")
        danhMucRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                BaiTapChinh.clear()
                for (child in snapshot.children) {
                    val id = child.child("ID").getValue(Int::class.java)
                    val name = child.child("TenBaiTap").getValue(String::class.java)
                    if (id != null && name != null) {
                        BaiTapChinh[id] = name
                    }
                }
                notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
