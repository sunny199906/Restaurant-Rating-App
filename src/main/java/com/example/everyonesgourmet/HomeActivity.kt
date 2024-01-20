package com.example.everyonesgourmet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isInvisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.everyonesgourmet.adapter.AutoCompleteRestaurantAdapter
import com.example.everyonesgourmet.adapter.RestaurantAdapter
import com.example.everyonesgourmet.adapter.TabAdapter
import com.example.everyonesgourmet.kotlin_class.DownloadImageTask
import com.example.everyonesgourmet.kotlin_class.Restaurant
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val db = Firebase.firestore

    private lateinit var navDrawer:DrawerLayout
    private lateinit var userIcon:ImageView
    private lateinit var userName:TextView
    private lateinit var nav_view:NavigationView
    private lateinit var restaurantSearch:AutoCompleteTextView
    private lateinit var adapter: AutoCompleteRestaurantAdapter
    private var restaurantObjList = ArrayList<Restaurant>()
    private lateinit var floatingButton:FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        floatingButton = findViewById(R.id.floatingActionButton)
        if(getUserType()=="guest"){
            floatingButton.isInvisible=true
            floatingButton.isClickable=false
        }

        val toolbar = findViewById<Toolbar>(R.id.mtoolbar)
        //toolbar.setLogo(R.drawable.title_image_dark_yellow_green)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        restaurantSearch = findViewById(R.id.find_restaurant_input)
        fillRestaurantList()
        navDrawer = findViewById(R.id.drawer_layout)

        val drawerToggle = ActionBarDrawerToggle(this, navDrawer, toolbar,
            R.string.nav_drawer_open, R.string.nav_drawer_close)

        navDrawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        nav_view = findViewById(R.id.nav_view)
        nav_view.menu.clear()

        if(getUserType()==getString(R.string.preference_usertype_user)) {
            nav_view.inflateMenu(R.menu.drawer_menu_user)
            nav_view.inflateHeaderView(R.layout.nav_header)
            setUpNavHeader()
        }else if(getUserType()==getString(R.string.preference_usertype_guest))
            nav_view.inflateMenu(R.menu.drawer_menu_guest)

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(R.id.nav_homepage)

        val tab_layout = findViewById<TabLayout>(R.id.tab_layout)
        val view_pager = findViewById<ViewPager2>(R.id.view_pager)
        view_pager.setUserInputEnabled(false)

        val tabTitles = resources.getStringArray(R.array.homepage_tab_array)
        view_pager.adapter = TabAdapter(this)
        TabLayoutMediator(tab_layout, view_pager){ tab, position ->
            tab.text = tabTitles[position]
        }.attach()


    }

    override fun onBackPressed() {
        if(navDrawer.isDrawerOpen(GravityCompat.START)) {
            navDrawer.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        //menuInflater.inflate((R.menu.toolbar_layout),menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        if(getUserType()==getString(R.string.preference_usertype_user))
            when (item.itemId) {
                R.id.nav_homepage -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_editProfile -> {
                    val intent = Intent(this, EditUserInfoActicity::class.java)
                    startActivity(intent)
                }
                R.id.nav_reviewHistory -> {
                    val intent = Intent(this, ReviewHistoryActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_logout -> {
                    setLogoutSharedPreference()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        else if(getUserType()==getString(R.string.preference_usertype_guest))
            when (item.itemId) {
                R.id.nav_login ->{
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        navDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun getUserType():String{
        val sharedPref = getSharedPreferences(getString(R.string.preference_user_file_key), Context.MODE_PRIVATE)
        return sharedPref.getString(getString(R.string.preference_key_usertype),null)!!

    }

    fun setUpNavHeader(){
        var headerLayout = nav_view.getHeaderView(0)
        userIcon = headerLayout.findViewById(R.id.user_icon)
        userName = headerLayout.findViewById(R.id.username)

        //get data from db
        db.collection(getString(R.string.firestore_user_collection_key))
            .document(getSharedPreferences(getString(R.string.preference_user_file_key),
                Context.MODE_PRIVATE).getString(getString(R.string.preference_key_userID),
                null)!!).get().addOnSuccessListener {
                DownloadImageTask(userIcon).execute(it.getString(
                    getString(R.string.fireStore_image_field_name)))

                userName.setText(it.getString(
                    getString(R.string.fireStore_username_field_name)))
            }
    }

    fun setLogoutSharedPreference(){
        val sharedPref = getSharedPreferences(getString(R.string.preference_user_file_key), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        if (!sharedPref.contains(getString(R.string.preference_key_userID))) {
            editor.putString(getString(R.string.preference_key_userID), null)
            editor.putString(getString(R.string.preference_key_password), null)
            editor.putString(getString(R.string.preference_key_usertype), null)
        }
        editor.commit()
    }

    fun reviewClicked(view:View){
        val intent = Intent(this, ReviewActivity::class.java)
        startActivity(intent)
    }

    fun openMap(view:View){
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
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
        restaurantSearch.setAdapter(adapter)
        restaurantSearch.setOnItemClickListener { parent, view, position, id ->
            var selectedRestaurant = parent.getItemAtPosition(position) as Restaurant
            var intent = Intent(this, RestaurantDetailsActivity::class.java)
            intent.putExtra("documentID", selectedRestaurant.getDBKey())
            startActivity(intent)
        }
    }
}