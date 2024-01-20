package com.example.everyonesgourmet.kotlin_class

import android.graphics.Bitmap

class Review(iReviewID:String, iUserName:String, iUserIcon:String, iRating:Float, iReviewImage:String, iReviewComment:String, iBgColor:String) {

    private var reviewID = iReviewID
    private var userName = iUserName
    private var userIcon = iUserIcon
    private var rating = iRating
    private var reviewImage = iReviewImage
    private var reviewComment = iReviewComment
    private var color = iBgColor

    fun getReviewID()=reviewID
    fun getUsername()=userName
    fun getUserIcon()=userIcon
    fun getReting()=rating
    fun getReviewImage()=reviewImage
    fun getReviewComment()=reviewComment
    fun getBgColor()=color

    fun setUserIcon(imageUrl:String){
        userIcon=imageUrl
    }
}