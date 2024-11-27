package com.example.myadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myadmin.adapater.RatingAdapter
import com.google.firebase.database.*
import data.Ratings

class DanhGiaFragment : Fragment(R.layout.fragment_danh_gia) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var ratingAdapter: RatingAdapter
    private lateinit var database: DatabaseReference
    private var ratings = mutableListOf<Ratings>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewCategories)
        database = FirebaseDatabase.getInstance().getReference("ratings")

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        ratingAdapter = RatingAdapter(ratings)
        recyclerView.adapter = ratingAdapter

        // Load ratings from Firebase
        loadRatings()

    }

    private fun loadRatings() {
        // Assuming you are loading ratings for a specific product, e.g., productId
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ratings.clear()
                for (child in snapshot.children) {
                    val rating = child.getValue(Ratings::class.java)
                    rating?.let { ratings.add(it) }
                }
                ratingAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

}
