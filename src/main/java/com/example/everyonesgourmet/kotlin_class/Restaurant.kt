package com.example.everyonesgourmet.kotlin_class

import android.graphics.Bitmap
import android.media.Rating
import android.util.Log
import com.google.firebase.firestore.GeoPoint
import java.util.ArrayList

class Restaurant(rdbKey:String, rname: String, ricon: String, rdes:String, roverallrating: Float,
                 rfoodrating: Float, renvironmentrating: Float, rattituderating: Float,
                 rtype:ArrayList<String>, rlocation:GeoPoint, raddress:String) {

    private var dbKey:String=rdbKey
    private var name:String=rname
    private var image:String=ricon
    private var description:String=rdes
    private var overallRating:Float=roverallrating
    private var foodRating:Float=rfoodrating
    private var environmentRating:Float=renvironmentrating
    private var attitudeRating:Float=rattituderating
    private var type:ArrayList<String> = rtype
    private var location:GeoPoint = rlocation
    private var address:String = raddress

    fun setName(name:String) {
        this.name=name
    }

    fun getName(): String {
        return name.toString();
    }

    fun setImage(newIcon:String){
        image=newIcon
    }

    fun getImage(): String {
        return image;
    }

    fun setDescription(description:String) {
        this.description=description
    }

    fun getDescription(): String {
        return description;
    }

    fun setOverallRating(newRating:Float){
        overallRating=newRating
    }

    fun getOverallRating(): Float {
        return overallRating;
    }

    fun setFoodRating(newRating:Float){
        foodRating=newRating
    }

    fun getFoodRating(): Float {
        return foodRating;
    }

    fun setEnvironmentRating(newRating:Float){
        environmentRating=newRating
    }

    fun getEnvironmentRating(): Float {
        return environmentRating;
    }

    fun setAttitudeRating(newRating:Float){
        attitudeRating=newRating
    }

    fun getAttitudeRating(): Float {
        return attitudeRating;
    }

    fun setRestaurantType(newType:ArrayList<String>){
        type=newType
    }

    fun getRestaurantType():ArrayList<String>{
        return type
    }


    fun setGeopoint(geoPoint: GeoPoint){
        location=geoPoint
    }

    fun getLatitude():Double{
        return location.latitude
    }

    fun getLongitude():Double{
        return location.longitude
    }

    fun getDBKey():String{
        return dbKey
    }

    fun getAddress():String{
        return address
    }
}