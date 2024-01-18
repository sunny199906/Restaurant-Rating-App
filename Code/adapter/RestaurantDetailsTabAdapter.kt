package com.example.everyonesgourmet.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.everyonesgourmet.fragment.RestaurantDetailsCommentFragment
import com.example.everyonesgourmet.fragment.RestaurantDetailsFragment
import com.example.everyonesgourmet.kotlin_class.Restaurant

class RestaurantDetailsTabAdapter(activity: AppCompatActivity, iRestaurant:Restaurant) : FragmentStateAdapter(activity){

    var restaurantObj:Restaurant = iRestaurant

    override fun getItemCount(): Int {
          return 2
    }

    override fun createFragment(position: Int): Fragment {
          when(position){
              0 -> return RestaurantDetailsFragment(restaurantObj)
              1 -> return RestaurantDetailsCommentFragment(restaurantObj)
          }
        return RestaurantDetailsFragment(restaurantObj)
    }

}