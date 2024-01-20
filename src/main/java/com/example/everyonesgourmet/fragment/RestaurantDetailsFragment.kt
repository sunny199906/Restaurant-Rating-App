package com.example.everyonesgourmet.fragment

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import com.example.everyonesgourmet.R
import com.example.everyonesgourmet.ReviewActivity
import com.example.everyonesgourmet.kotlin_class.Restaurant
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RestaurantDetailsFragment(iRestaurant: Restaurant) : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap


    val restObj:Restaurant = iRestaurant
    private lateinit var mapView:MapView
    private lateinit var overallRatingBar: RatingBar
    private lateinit var foodRatingBar: RatingBar
    private lateinit var environmentRatingBar: RatingBar
    private lateinit var attitudeRatingBar: RatingBar
    private lateinit var description: TextView
    private lateinit var reviewButton:FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?):View{

        return inflater.inflate(R.layout.fragment_restaurant_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        initWidget(view)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onMapReady(map: GoogleMap) {
        this.googleMap=map
        val restaurantLocation = LatLng(restObj.getLatitude(), restObj.getLongitude())
        googleMap.addMarker(MarkerOptions().position(restaurantLocation).title(restObj.getName()))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(restaurantLocation))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(restaurantLocation, 15f))

    }

    fun initWidget(view: View){
        overallRatingBar=view.findViewById(R.id.overallRatingBar)
        foodRatingBar=view.findViewById(R.id.foodRatingBar)
        environmentRatingBar=view.findViewById(R.id.environmentRatingBar)
        attitudeRatingBar=view.findViewById(R.id.attitudeRatingBar)
        description=view.findViewById(R.id.restaurantDescription)
        reviewButton = view.findViewById(R.id.reviewFloatingActionButton)

        if(getUserType()=="guest"){
            reviewButton.isInvisible=true
            reviewButton.isClickable=false
        }

        reviewButton.setOnClickListener {
            var intent = Intent(it.context, ReviewActivity::class.java)
            intent.putExtra("id", restObj.getDBKey())
            it.context.startActivity(intent)
        }

        overallRatingBar.rating=restObj.getOverallRating()
        foodRatingBar.rating=restObj.getFoodRating()
        environmentRatingBar.rating=restObj.getEnvironmentRating()
        attitudeRatingBar.rating=restObj.getAttitudeRating()
        description.setText(restObj.getDescription())
    }

    fun getUserType():String{
        val sharedPref = this.activity?.getSharedPreferences(getString(R.string.preference_user_file_key), Context.MODE_PRIVATE)
        return sharedPref?.getString(getString(R.string.preference_key_usertype),null)!!

    }

}