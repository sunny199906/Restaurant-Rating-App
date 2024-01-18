package com.example.everyonesgourmet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.everyonesgourmet.adapter.RestaurantDetailsTabAdapter
import com.example.everyonesgourmet.kotlin_class.DownloadImageTask
import com.example.everyonesgourmet.kotlin_class.Restaurant
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class RestaurantDetailsActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var restaurant:Restaurant
    private lateinit var restaurantImageView:ImageView
    private lateinit var restaurantName:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)


        restaurantImageView=findViewById(R.id.restaurantImage)
        restaurantName=findViewById(R.id.restaurantName)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.mtoolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val tablayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager2 = findViewById<ViewPager2>(R.id.view_pager)

        db.collection("restaurant").document(intent.extras!!.get("documentID").toString())
            .get().addOnSuccessListener { document ->
                val restaurantObj = Restaurant(
                    document.id,
                    document.get("name").toString(),
                    document.get("image").toString(),
                    document.get("description").toString(),
                    document.getDouble("overallRating")!!.toFloat(),
                    document.getDouble("foodRating")!!.toFloat(),
                    document.getDouble("environmentRating")!!.toFloat(),
                    document.getDouble("attitudeRating")!!.toFloat(),
                    document.get("type") as ArrayList<String>,
                    document.getGeoPoint("location")!!,
                    document.get("address").toString()
                )

                restaurant=restaurantObj
                setUpRestaurantDetails(restaurantObj.getName(), restaurantObj.getImage())
                viewPager2.setUserInputEnabled(false)

                val tabTitleArray = resources.getStringArray(R.array.restaurant_details_tab_array)
                viewPager2.adapter = RestaurantDetailsTabAdapter(this, restaurantObj)
                TabLayoutMediator(tablayout, viewPager2){tab, position ->
                    tab.text = tabTitleArray[position]
                }.attach()


        }

    }

    fun setUpRestaurantDetails(restaurantNameString:String, restaurantImageUrl:String){
        restaurantName.setText(restaurantNameString)
        DownloadImageTask(restaurantImageView)
            .execute(restaurantImageUrl)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    fun upButtonClick(view: View) {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }



}