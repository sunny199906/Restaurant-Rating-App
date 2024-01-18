package com.example.everyonesgourmet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegistrationActivity :AppCompatActivity() {

    private val db = Firebase.firestore
    lateinit var usernameInput:TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        usernameInput = findViewById(R.id.username)
    }

    fun signUpClicked(view: View){

        val passwordInput = findViewById<TextInputLayout>(R.id.password)
        val passwordConfirmInput = findViewById<TextInputLayout>(R.id.password_confirm)

        val username = usernameInput.editText?.text.toString()
        val password = passwordInput.editText?.text.toString()
        val passwordConfirm = passwordConfirmInput.editText?.text.toString()

        if(!checkIfEmpty(username, password, passwordConfirm)){
            if(password.equals(passwordConfirm)){
                checkIfUserExist(username, password)
            }else{
                passwordInput.error="Unequal password"
                passwordConfirmInput.error="Unequal password"
            }
        }else{
            //add error to others
            if(username=="")
                usernameInput.error="Missing"
            else
                usernameInput.error=null

            if(password=="")
                passwordInput.error="Missing"
            else
                passwordInput.error=null

            if(passwordConfirm=="")
                passwordConfirmInput.error="Missing"
            else
                passwordConfirmInput.error=null
        }
    }

    fun checkIfEmpty(username: String, password: String, passwordConfirm:String) : Boolean{

        if(username!=null&&password!=null&&passwordConfirm!=null){
            if(username!=""&&password!=""&&passwordConfirm!=""){
                return false
            }
        }
        return true
    }

    fun checkIfUserExist(username: String, password: String){

        db.collection("user")
            .whereEqualTo("username", username)
            .get().addOnSuccessListener {

                if(it.documents.size==1){
                    usernameInput.error=getString(R.string.error_user_exist)
                }else{
                    writeUserDataIn(username, password)
                }

            }

    }

    fun writeUserDataIn(username:String, password:String){
        val userObj = hashMapOf(
            "username" to username,
            "password" to password,
            "icon" to "https://firebasestorage.googleapis.com/v0/b/cs306coursework-9ea7b.appspot.com/o/user%2Fuserdefaulticon.jpg?alt=media&token=da83afb2-7cd8-4e88-878c-9c96bf48b882"
        )

        db.collection("user").document(username)
            .set(userObj, SetOptions.merge())
            .addOnSuccessListener {
                setSharedPreference(username, password, "https://firebasestorage.googleapis.com/v0/b/cs306coursework-9ea7b.appspot.com/o/user%2Fuserdefaulticon.jpg?alt=media&token=da83afb2-7cd8-4e88-878c-9c96bf48b882")
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e -> Toast.makeText(this, "Error", Toast.LENGTH_LONG).show() }
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