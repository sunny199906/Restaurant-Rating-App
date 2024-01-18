package com.example.everyonesgourmet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.everyonesgourmet.databinding.ActivityMapsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        db.collection("restaurant").get().addOnSuccessListener { documents->

            for(document in documents){
                val restaurantLocation = LatLng(document.getGeoPoint("location")!!.latitude, document.getGeoPoint("location")!!.longitude)
                mMap.addMarker(MarkerOptions().position(restaurantLocation).title(document.getString("name")))

            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(51.61851929969005, -3.8812708339103508)))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(51.61851929969005, -3.8812708339103508), 15f))
        }
        // Add a marker in Sydney and move the camera

    }
}