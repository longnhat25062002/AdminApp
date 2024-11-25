package com.example.myadmin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import com.example.adminpt.fragments.FrequencyFragment
import com.example.myadmin.fragments.MainWorkoutFragment
import com.example.adminpt.fragments.StretchingFragment
import com.example.adminpt.fragments.WarmupFragment
import com.example.myadmin.fragments.ReportFragment
import com.example.myadmin.fragments.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kích hoạt tính năng Edge-to-Edge
        enableEdgeToEdge()

        // Thiết lập layout cho Activity
        setContentView(R.layout.activity_main)

        // Ánh xạ DrawerLayout và NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        // Cài đặt Toolbar làm ActionBar và Drawer Toggle
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Đặt mặc định là Fragment Bài Tập chính
        if (savedInstanceState == null) {
            loadFragment(MainWorkoutFragment())  // Fragment đầu tiên là Bài Tập chính
        }

        // Cấu hình NavigationView item listener
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.main_workout -> {
                    loadFragment(MainWorkoutFragment()) // Fragment cho Bài Tập chính
                    true
                }
                R.id.explore -> {
                    loadFragment(ExploreFragment()) // Fragment cho Khám Phá
                    true
                }
                R.id.warmup -> {
                    loadFragment(WarmupFragment()) // Fragment cho Bài tập khởi động
                    true
                }
                R.id.stretching -> {
                    loadFragment(StretchingFragment()) // Fragment cho Bài tập giãn cơ
                    true
                }
                R.id.frequency -> {
                    loadFragment(FrequencyFragment()) // Fragment cho Tần suất luyện tập
                    true
                }
                R.id.report -> {
                    loadFragment(ReportFragment()) // Fragment cho Báo cáo
                    true
                }
                else -> false
            }.also {
                // Đóng Drawer khi chọn mục
                drawerLayout.closeDrawers()
            }
        }

        // Xử lý sự kiện cho nút hamburger trong Toolbar để mở/đóng Drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        // Ánh xạ và xử lý WindowInsets cho việc mở rộng các cạnh màn hình
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Hàm để thay đổi fragment
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
