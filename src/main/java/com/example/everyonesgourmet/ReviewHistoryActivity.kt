package com.example.everyonesgourmet

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.everyonesgourmet.adapter.ReviewAdapter
import com.example.everyonesgourmet.kotlin_class.Review
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class ReviewHistoryActivity : AppCompatActivity() {

    private lateinit var toolbar:Toolbar
    private lateinit var reviewHistoryView:RecyclerView

    private val db = Firebase.firestore
    private var reviewList = ArrayList<Review>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_history)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        reviewHistoryView = findViewById(R.id.reviewHistory)
        val layoutManager = LinearLayoutManager(this)
        reviewHistoryView.layoutManager = layoutManager
        val mAdapter = ReviewAdapter(reviewList, true)
        reviewHistoryView.adapter = mAdapter

        setUpHistory()
    }

    fun setUpHistory(){
        val sharedPref =
            getSharedPreferences(getString(R.string.preference_user_file_key), Context.MODE_PRIVATE)
        //Toast.makeText(this, sharedPref.getString(getString(R.string.preference_key_userID), null), Toast.LENGTH_LONG).show()
        db.collection("review")
            .whereEqualTo("userID", sharedPref.getString(getString(R.string.preference_key_userID),
                null))
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
                    reviewList.add(review)
                }
                setUpRecyclerView()
            }
    }

    fun setUpRecyclerView(){

        val mAdapter = ReviewAdapter(reviewList, true)
        reviewHistoryView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }
}