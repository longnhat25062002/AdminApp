package com.example.myadmin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.example.myadmin.R

class ReportFragment : Fragment(R.layout.fragment_report) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bạn có thể thêm các logic báo cáo ở đây, ví dụ như thống kê số liệu
        // Hiển thị báo cáo từ Firebase hoặc các dữ liệu khác
        view.findViewById<View>(R.id.btnViewReport).setOnClickListener {
            Toast.makeText(context, "Xem báo cáo", Toast.LENGTH_SHORT).show()
        }
    }
}
