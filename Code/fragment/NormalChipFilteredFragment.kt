package com.example.everyonesgourmet.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import java.util.ArrayList
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.everyonesgourmet.R
import com.example.everyonesgourmet.kotlin_class.Restaurant
import com.example.everyonesgourmet.adapter.RestaurantAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class NormalChipFilteredFragment : Fragment() {

    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()
    private var restaurantList = ArrayList<Restaurant>()
    private lateinit var recyclerViewList:RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_normal_chip_filtered, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewList = view.findViewById(R.id.ResList)
        val layoutManager = LinearLayoutManager(activity)
        recyclerViewList.layoutManager = layoutManager
        val mAdapter = RestaurantAdapter(restaurantList)
        recyclerViewList.adapter = mAdapter
        populateRestaurantList(view, savedInstanceState)

    }

    private fun populateRestaurantList(view: View, savedInstanceState: Bundle?) {
        db.collection("restaurant").get().addOnSuccessListener { documentCollection ->
            for (document in documentCollection) {
                var restaurant = Restaurant(
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
                restaurantList.add(restaurant)

            }
            setUpRecyclerView(view, savedInstanceState)
        }
        /*val restaurantNameList = arrayOf(R.string.restaurant1Name, R.string.restaurant2Name, R.string.restaurant3Name)
        val restaurantImageList = arrayOf(R.drawable.restaurant1, R.drawable.restaurant2, R.drawable.restaurant3)
        val restaurantDescriptionList = arrayOf(R.string.restaurant1Description, R.string.restaurant2Description, R.string.restaurant3Description)
        val restaurantRatingList = arrayOf(5.0f,4.0f,3.0f)
for (i in 0..2) {


val res = Restaurant(getString(restaurantNameList[i]), restaurantImageList[i]
, restaurantRatingList[i], getString(restaurantDescriptionList[i]))
restaurantList.add(res)
}
    */
    }

    private fun setUpRecyclerView(view: View, savedInstanceState: Bundle?) {
        val mAdapter = RestaurantAdapter(restaurantList)
        recyclerViewList.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

}