package com.example.myadmin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.Exercise

class ExerciseAdapter(
    private var exercises: MutableList<Exercise>, // Sử dụng MutableList để dễ dàng thay đổi dữ liệu
    private val onEdit: (Exercise) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    inner class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvExerciseName)
        val tvPart: TextView = view.findViewById(R.id.tvExercisePart)
        val tvLevel: TextView = view.findViewById(R.id.tvExerciseLevel)
        val tvReps: TextView = view.findViewById(R.id.tvExerciseReps)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.tvName.text = exercise.TenBaiTap // Hiển thị tên bài tập
        holder.tvPart.text = exercise.BoPhan // Hiển thị bộ phận cơ thể
        holder.tvLevel.text = exercise.MucDo // Hiển thị mức độ bài tập
        holder.tvReps.text = exercise.SoRep.toString() // Hiển thị số rep

        // Xử lý nút sửa
        holder.btnEdit.setOnClickListener { onEdit(exercise) }

        // Xử lý nút xóa
        holder.btnDelete.setOnClickListener {
            onDelete(exercise.ID)
            removeItem(position) // Xóa bài tập khỏi danh sách
        }
    }

    override fun getItemCount(): Int = exercises.size

    // Hàm thêm bài tập mới vào danh sách
    fun addItem(exercise: Exercise) {
        exercises.add(exercise) // Thêm bài tập vào cuối danh sách
        notifyItemInserted(exercises.size - 1) // Thông báo cập nhật UI
    }

    // Hàm sửa bài tập trong danh sách
    fun updateItem(updatedExercise: Exercise) {
        val position = exercises.indexOfFirst { it.ID == updatedExercise.ID }
        if (position != -1) {
            exercises[position] = updatedExercise // Cập nhật bài tập trong danh sách
            notifyItemChanged(position) // Thông báo cập nhật UI
        }
    }

    // Hàm xóa bài tập khỏi danh sách
    fun removeItem(position: Int) {
        if (position >= 0 && position < exercises.size) {
            exercises.removeAt(position) // Xóa bài tập tại vị trí chỉ định
            notifyItemRemoved(position) // Thông báo cập nhật UI
        }
    }

    // Hàm cập nhật danh sách khi tìm kiếm
    fun updateList(newList: List<Exercise>) {
        exercises.clear()
        exercises.addAll(newList) // Cập nhật danh sách với dữ liệu mới
        notifyDataSetChanged() // Thông báo cập nhật UI
    }
}
