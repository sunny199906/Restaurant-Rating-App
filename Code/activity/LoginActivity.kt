package com.example.everyonesgourmet

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.everyonesgourmet.kotlin_class.CheckNull
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var usernameTextInputLayout: TextInputLayout
    private lateinit var passwordTextInputLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        usernameTextInputLayout = findViewById(R.id.username)
        passwordTextInputLayout = findViewById(R.id.password)
        checkIfCanAutoInputInfo()
    }

    fun checkIfCanAutoInputInfo() {
        val sharedPref =
            getSharedPreferences(getString(R.string.preference_user_file_key), Context.MODE_PRIVATE)

        if (sharedPref.contains(getString(R.string.preference_key_userID))
            &&sharedPref.getString(getString(R.string.preference_key_userID)
                , null)!=null) {
            usernameTextInputLayout.editText!!
                .setText(
                    sharedPref.getString(
                        getString(
                            R.string.preference_key_userID
                        ), null
                    )
                )
            passwordTextInputLayout.editText!!
                .setText(
                    sharedPref.getString(
                        getString(
                            R.string.preference_key_password
                        ), null
                    )
                )
        }
    }

    fun loginClicked(view: View) {
        val username = usernameTextInputLayout.editText?.text.toString()
        val password = passwordTextInputLayout.editText?.text.toString()

        if (!CheckNull(username, password).checkifNullOrEmpty()) {
            canLogin(username, password)
        }
    }

    fun signUpButtonClicked(view: View) {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    fun canLogin(username: String, password: String) {
        db.collection(getString(R.string.firestore_user_collection_key))
            .whereEqualTo(getString(R.string.fireStore_username_field_name), username).get()
            .addOnCompleteListener {
                var correctPassword: String = ""
                if (it.isSuccessful) {
                    for (document in it.result!!) {
                        correctPassword =
                            document.get(getString(R.string.fireStore_password_field_name))
                                .toString()
                        if (correctPassword == password) {
                            setSharedPreference(username, password, document.getString("icon")!!)
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
    }

    fun setSharedPreference(userID: String, password: String, iconUrl:String) {
        val sharedPref =
            getSharedPreferences(getString(R.string.preference_user_file_key), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(getString(R.string.preference_key_userID), userID)
        editor.putString(getString(R.string.preference_key_password), password)
        editor.putString("userIcon", iconUrl)
        editor.putString(
            getString(R.string.preference_key_usertype),
            getString(R.string.preference_usertype_user)
        )
        editor.commit()
    }

}