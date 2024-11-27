package com.example.myadmin.adapater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myadmin.R
import data.ListBaiTapKhamPha


class ListBaiTapKhamPhaAdapter(private val khampha: MutableList<ListBaiTapKhamPha>,
                      private val onEdit: (ListBaiTapKhamPha) -> Unit,
                      private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<ListBaiTapKhamPhaAdapter.KhamPhaViewHolder>() {

    inner class KhamPhaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvExerciseName)
        val tvsorep: TextView = view.findViewById(R.id.tvExerciseReps)
        val tvtime: TextView = view.findViewById(R.id.tvExercisetime)
        val tvvideo: TextView = view.findViewById(R.id.txtExerciseVideo)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }
    // Tạo view holder cho từng mục trong RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KhamPhaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_khampha, parent, false)
        return KhamPhaViewHolder(view)
    }

    // Ràng buộc dữ liệu với các view trong mỗi item
    override fun onBindViewHolder(holder: KhamPhaViewHolder, position: Int) {
        val khampha = khampha[position]
        holder.tvName.text = "Tên danh mục : " + khampha.TenBaiTap
        holder.tvsorep.text = "Số rep : " + khampha.SoRep
        holder.tvtime.text = "Thời gian : " + khampha.ThoiGian
        holder.tvvideo.text = "Video : " + khampha.Video
        holder.btnEdit.setOnClickListener { onEdit(khampha) }

        // Xử lý nút xóa
        holder.btnDelete.setOnClickListener {
            onDelete(khampha.ID)

        }
    }

    // Số lượng item trong RecyclerView
    override fun getItemCount(): Int = khampha.size


}
