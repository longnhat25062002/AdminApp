package com.example.myadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myadmin.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class ReportFragment : Fragment(R.layout.fragment_report) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy reference đến Firebase Realtime Database
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        // Xác định tháng hiện tại
        val currentMonth = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date())

        // Kết quả báo cáo
        var maleCount = 0
        var femaleCount = 0
        var goalCount = 0
        var noWorkoutThisMonth = 0
        var newUsersThisMonth = 0

        // Lắng nghe dữ liệu từ Firebase
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)

                    if (user != null) {
                        // Phân loại theo giới tính
                        if (user.gender == "Nam") {
                            maleCount++
                        } else if (user.gender == "Nữ") {
                            femaleCount++
                        }

                        // Kiểm tra người dùng có workout trong tháng này không
                        val lastWorkoutDate = user.lastWorkoutDate
                        val currentYearMonth = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date())

                        if (user.monthlyWorkouts > 0) {
                            // Kiểm tra người dùng đăng ký mục tiêu "Giảm cân"
                            if (user.goal == "Giảm cân") {
                                goalCount++
                            }
                        } else {
                            if (lastWorkoutDate.isEmpty() || lastWorkoutDate.substring(0, 7) != currentYearMonth) {
                                noWorkoutThisMonth++
                            }
                        }

                        // Kiểm tra người dùng đăng ký trong tháng này
                        if (lastWorkoutDate.isNotEmpty() && lastWorkoutDate.substring(0, 7) == currentYearMonth) {
                            newUsersThisMonth++
                        }
                    }
                }

                // Cập nhật giao diện với báo cáo
                val reportText = """
                    Số lượng người dùng Nam: $maleCount
                    Số lượng người dùng Nữ: $femaleCount
                    Số lượng người dùng có mục tiêu 'Giảm cân': $goalCount
                    Số lượng người không tập trong tháng này: $noWorkoutThisMonth
                    Số lượng người dùng mới đăng ký trong tháng này: $newUsersThisMonth
                """.trimIndent()

                // Hiển thị báo cáo trong TextView
                val reportTextView: TextView = view.findViewById(R.id.reportTextView)  // Đảm bảo có TextView trong layout XML
                reportTextView.text = reportText
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xử lý lỗi nếu có
                Toast.makeText(context, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Hiển thị Toast khi nhấn nút xem báo cáo (Tuỳ chọn, có thể thay thế bằng hành động khác)

    }

    // Đối tượng User tương ứng với cấu trúc dữ liệu Firebase
    data class User(
        val gender: String = "",
        val goal: String = "",
        val lastWorkoutDate: String = "",
        val monthlyWorkouts: Int = 0
    )
}
