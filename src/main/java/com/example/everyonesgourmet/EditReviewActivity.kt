package com.example.everyonesgourmet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.everyonesgourmet.kotlin_class.DownloadImageTask
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditReviewActivity : AppCompatActivity() {


    private val db = Firebase.firestore
    private lateinit var reviewID:String
    private lateinit var toolbar: Toolbar
    private lateinit var reviewImage:ImageView
    private lateinit var foodRating:RatingBar
    private lateinit var environmentRating:RatingBar
    private lateinit var attitudeRating:RatingBar
    private lateinit var reviewContent: TextInputLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_review)

        reviewID=intent.extras!!.getString("id")!!
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        initWidge()
        preLoadData()
    }

    fun preLoadData(){
        db.collection("review").document(reviewID)
            .get().addOnSuccessListener { document->
                DownloadImageTask(reviewImage).execute(document.getString("reviewImage"))
                foodRating.rating=document.get("foodRating")!!.toString().toFloat()
                environmentRating.rating=document.get("environmentRating")!!.toString().toFloat()
                attitudeRating.rating=document.get("attitudeRating")!!.toString().toFloat()
                reviewContent.editText!!.setText(document.getString("reviewContent"))
            }
    }

    fun updateReview(view:View){
        val review = hashMapOf(
            "overallRating" to (foodRating.rating+environmentRating.rating+attitudeRating.rating)/3,
            "foodRating" to foodRating.rating,
            "environmentRating" to environmentRating.rating,
            "attitudeRating" to attitudeRating.rating,
            "reviewContent" to reviewContent.editText!!.text.toString(),
        )
        db.collection("review").document(reviewID)
            .set(review, SetOptions.merge()).addOnSuccessListener {
                var intent = Intent(this, ReviewHistoryActivity::class.java)
                startActivity(intent)
        }

    }

    fun initWidge(){
        reviewImage = findViewById(R.id.review_image)
        foodRating = findViewById(R.id.foodRating)
        environmentRating = findViewById(R.id.environmentRating)
        attitudeRating = findViewById(R.id.attitudeRating)
        reviewContent = findViewById(R.id.review_input)
    }
}