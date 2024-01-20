package com.example.everyonesgourmet

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.everyonesgourmet.adapter.AutoCompleteRestaurantAdapter
import com.example.everyonesgourmet.kotlin_class.Restaurant
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.*

class ReviewActivity() : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var previewImageView: ImageView

    private lateinit var restaurantInputLayout: AutoCompleteTextView
    private lateinit var foodRating: RatingBar
    private lateinit var environmentRating: RatingBar
    private lateinit var attitudeRating: RatingBar
    private lateinit var reviewInputLayout: TextInputLayout

    private lateinit var restaurantObjList: ArrayList<Restaurant>
    private lateinit var adapter: AutoCompleteRestaurantAdapter
    private val db = Firebase.firestore
    private val storageRef = FirebaseStorage.getInstance()
    private lateinit var imageRef: StorageReference

    private lateinit var selectedRestaurant: Restaurant

    private lateinit var bgSpinner: Spinner
    private lateinit var formBg:ConstraintLayout

    private val pickImage = 100
    private var imageUri: Uri? = null

    private var selectedBgColor = "white"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(getString(R.string.reviewButtonText))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        restaurantObjList = ArrayList<Restaurant>()
        initWidget()
        fillRestaurantList()
        checkAndSetSelectedRestaurant()
        setSpinner()
    }

    fun setSpinner(){
        bgSpinner = findViewById(R.id.bgSpinner)
        formBg = findViewById(R.id.formBg)
        ArrayAdapter.createFromResource(
            this,
            R.array.bg_style_array,
            android.R.layout.simple_spinner_item
        ).also { adapter->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bgSpinner.adapter = adapter
        }

        bgSpinner.onItemSelectedListener=this
    }

    fun uploadImage(view: View) {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)
    }

    fun reviewButtonClicked(view: View) {
        val sharedPref = getSharedPreferences(getString(R.string.preference_user_file_key), Context.MODE_PRIVATE)

        if (selectedRestaurant != null) {

            val path = "review/" + UUID.randomUUID() + ".jpeg"
            imageRef = storageRef.getReference(path)
            var uploadTask = imageRef.putBytes(getImageBitmap())
            uploadTask.addOnCompleteListener {
                if (it.isSuccessful) {
                    val downloadUri = imageRef.downloadUrl.addOnSuccessListener {

                        var overallRating =
                            (foodRating.rating + environmentRating.rating + attitudeRating.rating) / 3
                        val review = hashMapOf(
                            "restaurantID" to selectedRestaurant.getDBKey(),
                            "userID" to sharedPref.getString(getString(R.string.preference_key_userID),null),
                            "overallRating" to overallRating,
                            "foodRating" to foodRating.rating,
                            "environmentRating" to environmentRating.rating,
                            "attitudeRating" to attitudeRating.rating,
                            "reviewContent" to reviewInputLayout.editText!!.text.toString(),
                            "reviewImage" to it.toString(),
                            "bgColor" to selectedBgColor,
                            "userIcon" to sharedPref.getString("userIcon",null)
                        )
                        db.collection("review").add(review).addOnSuccessListener {

                            updateRatingOfRestaurant(
                                selectedRestaurant.getDBKey(), foodRating.rating,
                                environmentRating.rating, attitudeRating.rating
                            )
                            var intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                    }


                }
            }

        }
    }

    fun updateRatingOfRestaurant(
        restaurantID: String, foodRating: Float,
        environmentRating: Float, attitudeRating: Float
    ) {

        db.collection("restaurant").document(restaurantID).get().addOnSuccessListener {

            //var oRating = it.get("overallRating").toString().toFloat()
            var fRating = it.get("foodRating").toString().toFloat()
            var eRating = it.get("environmentRating").toString().toFloat()
            var aRating = it.get("attitudeRating").toString().toFloat()
            var totalReviewNumber = it.get("numberOfReview").toString().toFloat()
            Toast.makeText(this, fRating.toString(), Toast.LENGTH_LONG).show()
            var averagefRating = fRating / totalReviewNumber
            var averageeRating = eRating / totalReviewNumber
            var averageaRating = aRating / totalReviewNumber

            fRating = checkIfMaxed((foodRating + averagefRating)/(totalReviewNumber+1))
            eRating = checkIfMaxed((environmentRating + averageeRating)/(totalReviewNumber+1))
            aRating = checkIfMaxed((attitudeRating + averageaRating)/(totalReviewNumber+1))
            var oRating = (fRating + eRating + aRating) / 3
            totalReviewNumber += 1

            val updateData = hashMapOf(
                "overallRating" to oRating,
                "foodRating" to fRating,
                "environmentRating" to eRating,
                "attitudeRating" to aRating,
                "numberOfReview" to totalReviewNumber
            )

            db.collection("restaurant").document(restaurantID)
                .set(updateData, SetOptions.merge())

        }
    }

    fun checkIfMaxed(inValue: Float): Float {
        if (inValue > 5)
            return 5f
        else
            return inValue
    }

    fun checkAndSetSelectedRestaurant() {
        if (intent.extras != null) {
            db.collection("restaurant").document(intent.getStringExtra("id").toString()).get()
                .addOnSuccessListener { document ->
                    selectedRestaurant = Restaurant(
                        document.id,
                        document.get("name").toString(),
                        document.get("image").toString(),
                        document.get("description").toString(),
                        document.getDouble("overallRating")!!.toFloat(),
                        document.getDouble("foodRating")!!.toFloat(),
                        document.getDouble("environmentRating")!!.toFloat(),
                        document.getDouble("attitudeRating")!!.toFloat(),
                        document.get("type") as ArrayList<String>,
                        document.getGeoPoint("location")!!,
                        document.get("address").toString()
                    )
                    restaurantInputLayout.setText(selectedRestaurant.getName())
                }
        }
    }

    fun fillRestaurantList() {
        db.collection("restaurant").get().addOnSuccessListener { documentCollection ->
            for (document in documentCollection) {
                var restaurant = Restaurant(
                    document.id,
                    document.get("name").toString(),
                    document.get("image").toString(),
                    document.get("description").toString(),
                    document.getDouble("overallRating")!!.toFloat(),
                    document.getDouble("foodRating")!!.toFloat(),
                    document.getDouble("environmentRating")!!.toFloat(),
                    document.getDouble("attitudeRating")!!.toFloat(),
                    document.get("type") as ArrayList<String>,
                    document.getGeoPoint("location")!!,
                    document.get("address").toString()
                )
                restaurantObjList.add(restaurant)
            }
            populateAutoCompleteTextView()
        }
    }

    fun populateAutoCompleteTextView() {
        adapter = AutoCompleteRestaurantAdapter(this, restaurantObjList)
        restaurantInputLayout.setAdapter(adapter)
        restaurantInputLayout.setOnItemClickListener { parent, view, position, id ->
            selectedRestaurant = parent.getItemAtPosition(position) as Restaurant
        }
    }

    fun initWidget() {
        restaurantInputLayout = findViewById(R.id.find_restaurant_input)
        foodRating = findViewById(R.id.foodRating)
        environmentRating = findViewById(R.id.environmentRating)
        attitudeRating = findViewById(R.id.attitudeRating)
        reviewInputLayout = findViewById(R.id.review_input)
        previewImageView = findViewById(R.id.previewImage)
    }

    fun getImageBitmap(): ByteArray {
        previewImageView.isDrawingCacheEnabled = true
        previewImageView.buildDrawingCache()
        val bitmap = (previewImageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        return data
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {

            imageUri = data?.data
            previewImageView.setImageURI(imageUri)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var bgStyle = parent!!.getItemAtPosition(position).toString()
        selectedBgColor = bgStyle
        when(bgStyle){
            "white"->formBg.setBackgroundColor(Color.WHITE)
            "yellow"->formBg.setBackgroundColor(Color.YELLOW)
            "green"->formBg.setBackgroundColor(Color.GREEN)
            "red"->formBg.setBackgroundColor(Color.RED)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


}