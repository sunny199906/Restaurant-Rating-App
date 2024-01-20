package com.example.everyonesgourmet.kotlin_class

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import java.io.InputStream
import java.net.URL


class DownloadImageTask(bmImage:ImageView): AsyncTask<String, Void, Bitmap>() {

    var imageView:ImageView = bmImage

    override fun doInBackground(vararg urls: String): Bitmap? {
        val imageURL = urls[0]
        var image: Bitmap? = null
        try {
            val inputsteam = java.net.URL(imageURL).openStream()
            image = BitmapFactory.decodeStream(inputsteam)
        }
        catch (e: Exception) {
            Log.e("Error Message", e.message.toString())
            e.printStackTrace()
        }
        return image
    }

    override fun onPostExecute(result: Bitmap?) {
        imageView.setImageBitmap(result)
        super.onPostExecute(result)
    }

}