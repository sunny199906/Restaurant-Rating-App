package com.example.everyonesgourmet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.example.everyonesgourmet.R
import com.example.everyonesgourmet.kotlin_class.DownloadImageTask
import com.example.everyonesgourmet.kotlin_class.Restaurant

class AutoCompleteRestaurantAdapter(context: Context, restaurantList: List<Restaurant>) :
    ArrayAdapter<Restaurant>(context, 0, restaurantList) {

    private var restaurantListInFull = ArrayList<Restaurant>(restaurantList)

    override fun getFilter() = restaurantFilter

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var returnView:View
        if(convertView == null)
            returnView = LayoutInflater.from(context).inflate(R.layout.restaurant_autocomplete_row, parent, false)
        else
            returnView = convertView

        val restaurantName = returnView.findViewById<TextView>(R.id.restaurant_name)
        val restaurantAddress = returnView.findViewById<TextView>(R.id.restaurant_address)

        var restaurantItem = getItem(position)

        if(restaurantItem != null){
            restaurantName.setText(restaurantItem.getName())
            restaurantAddress.setText(restaurantItem.getAddress())
        }

        return returnView!!
    }

    private var restaurantFilter: Filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {

            var result = FilterResults();
            var suggestionList = ArrayList<Restaurant>()

            if(constraint == null || constraint.length == 0){
                suggestionList.addAll(restaurantListInFull)
            }else{
                var filterString = constraint.toString().lowercase().trim()

                for(restaurant in restaurantListInFull){
                    if(restaurant.getName().lowercase().contains(filterString)){
                        suggestionList.add(restaurant)
                    }
                }
            }

            result.values = suggestionList
            result.count = suggestionList.size
            return result
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            clear()
            addAll(results!!.values as MutableCollection<Restaurant>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as Restaurant).getName()
        }
    }
}