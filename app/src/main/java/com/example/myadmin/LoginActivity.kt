package com.example.myadmin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        // Khởi tạo Realtime Database
        database = FirebaseDatabase.getInstance().reference

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Kiểm tra người dùng từ Realtime Database
                checkUserCredentials(username, password)
            } else {
                Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserCredentials(username: String, password: String) {
        // Truy vấn trực tiếp username và password từ Realtime Database
        database.child("admin")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Lấy giá trị username và password từ Firebase
                        val usernameFromDB = snapshot.child("username").getValue(String::class.java)
                        val passwordFromDB = snapshot.child("password").getValue(String::class.java)
                        Log.d("usser",usernameFromDB.toString())
                        // Kiểm tra xem username và password có khớp không
                        if (usernameFromDB == username && passwordFromDB == password) {
                            // Đăng nhập thành công, chuyển sang trang chính
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Đóng màn hình đăng nhập
                        } else {
                            // Nếu mật khẩu hoặc tên đăng nhập sai
                            Toast.makeText(this@LoginActivity, "Sai mật khẩu hoặc tên đăng nhập", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Nếu không tìm thấy người dùng
                        Toast.makeText(this@LoginActivity, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Nếu có lỗi trong quá trình truy vấn
                    Toast.makeText(this@LoginActivity, "Lỗi kết nối với cơ sở dữ liệu", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
