package com.example.everyonesgourmet.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.everyonesgourmet.fragment.NormalChipFilteredFragment
import com.example.everyonesgourmet.fragment.TopRankedChipFilteredFragment

class TabAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return NormalChipFilteredFragment()
            1 -> return TopRankedChipFilteredFragment()
        }
        return NormalChipFilteredFragment()
    }

}