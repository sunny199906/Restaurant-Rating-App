package com.example.everyonesgourmet

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.everyonesgourmet.kotlin_class.DownloadImageTask
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class EditUserInfoActicity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var avatar: ImageView
    private lateinit var editImageButton: FloatingActionButton
    private lateinit var username: TextInputLayout
    private lateinit var oldPW: TextInputLayout
    private lateinit var newPW: TextInputLayout
    private lateinit var newConfirmPW: TextInputLayout
    private lateinit var toolBar:Toolbar
    private val storageRef = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info_edit)
        initWidge()
        fillInOriginalInfo()
        setPostImageButton()
    }

    fun initWidge() {
        avatar = findViewById(R.id.avatar)
        editImageButton = findViewById(R.id.editImageButton)
        username = findViewById(R.id.usernameInputField)
        oldPW = findViewById(R.id.oldPasswordInputField)
        newPW = findViewById(R.id.newPasswordInputField)
        newConfirmPW = findViewById(R.id.newConfirmPasswordInputField)
        toolBar = findViewById(R.id.toolbar)

        username.editText!!.isFocusable = false
        username.editText!!.isFocusableInTouchMode = false
        username.editText!!.isClickable = true

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    fun setPostImageButton(){
        editImageButton.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            var imageUri = data?.data
            avatar.setImageURI(imageUri)
        }
    }

    fun fillInOriginalInfo() {
        db.collection(getString(R.string.firestore_user_collection_key))
            .document(getUserID()).get().addOnSuccessListener { document ->
                DownloadImageTask(avatar).execute(document.getString("icon"))
                username.editText!!.setText(
                    document.getString(
                        getString(R.string.fireStore_username_field_name)
                    )
                )
            }
    }

    fun changeUserInfo(view: View) {
        var oldPWText = oldPW.editText!!.text.toString()
        if (checkInputCorrect())
            db.collection(getString(R.string.firestore_user_collection_key))
                .document(getUserID()).get().addOnSuccessListener {
                    if (it.getString(getString(R.string.fireStore_password_field_name))==oldPWText)
                       updateUserInfo()
                    else
                        oldPW.error="password not correct"
                }
    }

    fun checkInputCorrect(): Boolean {
        var oldPWText = oldPW.editText!!.text.toString()
        var newPWText = newPW.editText!!.text.toString()
        var newConfirmPWText = newConfirmPW.editText!!.text.toString()

        if (oldPWText.trim().length >= 0)
            oldPW.error = null
        if (newPWText.trim().length > 0)
            newPW.error = null
        if (newConfirmPWText.trim().length > 0)
            newConfirmPW.error = null

        if (oldPWText == null) {
            oldPW.error = "missing information"
            if (newPWText == null) {
                newPW.error = "missing information"
                if (newConfirmPWText == null) {
                    newConfirmPW.error = "missing information"
                }
            }
        }

        if (oldPWText.trim().length >= 0 && newPWText.trim().length > 0 &&
            newConfirmPWText.trim().length > 0
        )
            if (newPWText == newConfirmPWText)
                return true
            else
                newConfirmPW.error = "unmatch password"
        else {
            if (oldPWText == "")
                oldPW.error = "missing information"
            if (newPWText == "")
                newPW.error = "missing information"
            if (newConfirmPWText == "")
                newConfirmPW.error = "missing information"

        }
        return false
    }

    fun updateUserInfo() {
        val path = "user/" + getUserID() + ".jpeg"
        var imageRef = storageRef.getReference(path)
        imageRef.putBytes(getImageBitmap()).addOnCompleteListener { imageTask ->
            if (imageTask.isSuccessful) {
                imageRef.downloadUrl.addOnSuccessListener { url ->
                    var userNewInfo = hashMapOf(
                        getString(R.string.fireStore_password_field_name)
                                to newPW.editText!!.text.toString(),
                        getString(R.string.fireStore_image_field_name)
                                to url.toString()
                    )
                    db.collection(getString(R.string.firestore_user_collection_key))
                        .document(getUserID()).set(userNewInfo, SetOptions.merge())
                        .addOnSuccessListener {
                            var intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                }

            }
        }
    }

    fun getUserID(): String {
        val sharedPref =
            getSharedPreferences(getString(R.string.preference_user_file_key), Context.MODE_PRIVATE)
        return sharedPref.getString(getString(R.string.preference_key_userID), null)!!
    }

    fun getImageBitmap(): ByteArray {
        avatar.isDrawingCacheEnabled = true
        avatar.buildDrawingCache()
        val bitmap = (avatar.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        return data
    }
}