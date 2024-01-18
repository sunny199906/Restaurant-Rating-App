package com.example.everyonesgourmet.adapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast

import androidx.recyclerview.widget.RecyclerView
import com.example.everyonesgourmet.R
import com.example.everyonesgourmet.kotlin_class.Restaurant
import com.example.everyonesgourmet.RestaurantDetailsActivity
import com.example.everyonesgourmet.kotlin_class.DownloadImageTask
import com.google.rpc.context.AttributeContext
import java.net.URL

class RestaurantAdapter(private val restaurantArrayList: MutableList<Restaurant>) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.restaurant_row_layout, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurantInfo = restaurantArrayList[position]

        holder.setRestaurentID(restaurantInfo.getDBKey())
        //holder.imgIcon.setImageResource(R.drawable.restaurant1)
        DownloadImageTask(holder.imgIcon).execute(restaurantInfo.getImage())

        holder.resTitle.text = restaurantInfo.getName()
        holder.resType.setText(restaurantInfo.getRestaurantType().joinToString(separator = " "))
        holder.resRating.setRating(restaurantInfo.getOverallRating())
    }

    override fun getItemCount(): Int {
        return restaurantArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        lateinit var restaurantID:String
        var imgIcon = itemView.findViewById<View>(R.id.resIcon) as ImageView
        var resTitle = itemView.findViewById<View>(R.id.title) as TextView
        var resType = itemView.findViewById<View>(R.id.type) as TextView
        var resRating = itemView.findViewById<View>(R.id.ratingBar) as RatingBar

        init {
            itemView.setOnClickListener(this)
        }

        fun setRestaurentID(iResID:String){
            restaurantID=iResID
        }

        override fun onClick(v: View) {
            var intent = Intent(itemView.context, RestaurantDetailsActivity::class.java)
            intent.putExtra("documentID", restaurantID)
            itemView.context.startActivity(intent)
        }
    }
}