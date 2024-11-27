package com.example.myadmin.adapater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myadmin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import data.Ratings
import java.text.SimpleDateFormat
import java.util.*

class RatingAdapter(private val ratings: List<Ratings>) : RecyclerView.Adapter<RatingAdapter.RatingViewHolder>() {
    private val firestore =  FirebaseDatabase.getInstance().getReference("users")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rating, parent, false)
        return RatingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val rating = ratings[position]

        // Hiển thị rating
        holder.ratingText.text = "Rating: ${rating.rating}"

        // Định dạng thời gian từ timestamp
        val timestamp = rating.timestamp
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())  // Định dạng ngày/tháng/năm giờ:phút:giây
        val formattedDate = dateFormat.format(Date(timestamp))  // Chuyển timestamp thành định dạng

        holder.timestampText.text = "Timestamp: $formattedDate"
        // Hiển thị tên người dùng

        // Lấy tên người dùng từ Firestore
        getUserNameById(rating.id) { username ->
            holder.usernameText.text = "User: $username"
        }
    }

    private fun getUserNameById(userId: String, callback: (String) -> Unit) {
        firestore.child(userId)  // Truy cập theo userId
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val username = dataSnapshot.child("displayName").getValue(String::class.java) ?: "Unknown"
                        callback(username)
                    } else {
                        callback("Unknown")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    callback("Error")
                }
            })
    }
    override fun getItemCount() = ratings.size

    inner class RatingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ratingText: TextView = view.findViewById(R.id.textRating)
        val timestampText: TextView = view.findViewById(R.id.textTimestamp)
        val usernameText: TextView = view.findViewById(R.id.textUsername)  // Thêm TextView cho tên người dùng
    }
}
