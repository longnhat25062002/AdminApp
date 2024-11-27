package com.example.myadmin.adapater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myadmin.R
import data.Category


class CategoryAdapter(private val categories: MutableList<Category>,
                      private val onEdit: (Category) -> Unit,
                      private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.categoryName)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }
    // Tạo view holder cho từng mục trong RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    // Ràng buộc dữ liệu với các view trong mỗi item
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.tvName.text = "Tên danh mục : " + category.Ten
        holder.btnEdit.setOnClickListener { onEdit(category) }

        // Xử lý nút xóa
        holder.btnDelete.setOnClickListener {
            onDelete(category.ID)

        }
    }

    // Số lượng item trong RecyclerView
    override fun getItemCount(): Int = categories.size


}
