package com.example.everyonesgourmet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private var mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSharedPreference()

        mAuth.signInWithEmailAndPassword(
            getString(R.string.dbUsername),
            getString(R.string.dbPassword)
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = mAuth.currentUser
                val toast =
                    Toast.makeText(this, getString(R.string.dbLoginSuccessful), Toast.LENGTH_LONG)
                toast.show()
            } else {
                val toast =
                    Toast.makeText(this, getString(R.string.dbLoginUnsuccessful), Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }

    fun setSharedPreference(){
        val sharedPref = getSharedPreferences(getString(R.string.preference_user_file_key), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(getString(R.string.preference_key_userID), null)
        editor.putString(getString(R.string.preference_key_password), null)
        editor.putString(getString(R.string.preference_key_usertype), null)
        editor.commit()
    }

    fun signInOrRegister(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    fun signInAsGuest(view: View) {
        val sharedPref = getSharedPreferences(getString(R.string.preference_user_file_key), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(getString(R.string.preference_key_usertype),
            getString(R.string.preference_usertype_guest))
        editor.commit()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}