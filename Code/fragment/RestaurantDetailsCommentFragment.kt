package com.example.everyonesgourmet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.everyonesgourmet.R
import com.example.everyonesgourmet.adapter.RestaurantAdapter
import com.example.everyonesgourmet.adapter.ReviewAdapter
import com.example.everyonesgourmet.kotlin_class.Restaurant
import com.example.everyonesgourmet.kotlin_class.Review
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class RestaurantDetailsCommentFragment(iRestaurant: Restaurant) : Fragment() {

    private val restaurantInThisPage = iRestaurant
    private val db = Firebase.firestore
    private var reviewList = ArrayList<Review>()
    private lateinit var commentSection:RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_restaurant_details_comment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        commentSection = view.findViewById<View>(R.id.comment_recyclerView) as RecyclerView
        val layoutManager = LinearLayoutManager(context)
        commentSection.layoutManager = layoutManager
        val mAdapter = ReviewAdapter(reviewList, false)
        commentSection.adapter = mAdapter
        var task = db.collection("review")
            .whereEqualTo("restaurantID", restaurantInThisPage.getDBKey())
            .get().addOnSuccessListener { documentCollection ->
            for (document in documentCollection) {
                var review = Review(
                    document.id,
                    document.get("userID").toString(),
                    document.get("userIcon").toString(),
                    document.getDouble("overallRating")!!.toFloat(),
                    document.get("reviewImage").toString(),
                    document.get("reviewContent").toString(),
                    document.get("bgColor").toString()
                )

                //DownloadImageTask
                reviewList.add(review)
            }
            setUpRecyclerView()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    fun setUpRecyclerView(){

        val mAdapter = ReviewAdapter(reviewList, false)
        commentSection.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

}