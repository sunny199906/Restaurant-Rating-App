package com.example.everyonesgourmet

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }

    fun returnClick(view : View){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

}