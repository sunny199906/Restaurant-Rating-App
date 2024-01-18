package com.example.everyonesgourmet.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.everyonesgourmet.EditReviewActivity
import com.example.everyonesgourmet.R
import com.example.everyonesgourmet.RestaurantDetailsActivity
import com.example.everyonesgourmet.kotlin_class.DownloadImageTask
import com.example.everyonesgourmet.kotlin_class.Restaurant
import com.example.everyonesgourmet.kotlin_class.Review

class ReviewAdapter(private val reviewArrayList: MutableList<Review>, inputIsMyReview:Boolean) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
    private var isMyReview=inputIsMyReview

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.comment_row_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ReviewAdapter.ViewHolder, position: Int) {
        val reviewInfo = reviewArrayList[position]
        //holder.imgIcon.setImageResource(R.drawable.restaurant1)
        DownloadImageTask(holder.userIcon).execute(reviewInfo.getUserIcon())
        holder.userName.text = reviewInfo.getUsername()
        holder.overallRating.rating=reviewInfo.getReting()
        DownloadImageTask(holder.reviewImage).execute(reviewInfo.getReviewImage())
        holder.reviewComment.text=reviewInfo.getReviewComment()

        when(reviewInfo.getBgColor()){
            "white"->{
                holder.formBg.setBackgroundColor(Color.WHITE)
            }
            "yellow"->{
                holder.formBg.setBackgroundColor(Color.YELLOW)
            }
            "green"->{
                holder.formBg.setBackgroundColor(Color.GREEN)
            }
            "red"->{
                holder.formBg.setBackgroundColor(Color.RED)
            }
        }


        if(isMyReview) {
            holder.editButton.isClickable = true
            holder.editButton.visibility=View.VISIBLE
            holder.editButton.setOnClickListener {
                var intent = Intent(holder.editButton.context, EditReviewActivity::class.java)
                intent.putExtra("id", reviewInfo.getReviewID())
                holder.editButton.context.startActivity(intent)
            }

        }
    }

    override fun getItemCount(): Int {
        return reviewArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        var formBg = itemView.findViewById<View>(R.id.reviewBg) as androidx.appcompat.widget.LinearLayoutCompat
        var userIcon = itemView.findViewById<View>(R.id.user_icon) as ImageView
        var userName = itemView.findViewById<View>(R.id.user_name) as TextView
        var overallRating = itemView.findViewById<View>(R.id.overallRatingBar) as RatingBar
        var reviewImage = itemView.findViewById<View>(R.id.review_image) as ImageView
        var reviewComment = itemView.findViewById<View>(R.id.review_comment) as TextView
        var editButton = itemView.findViewById<View>(R.id.editButton) as Button

        init {
            itemView.setOnClickListener(this)
        }


        override fun onClick(v: View) {

        }
    }
}