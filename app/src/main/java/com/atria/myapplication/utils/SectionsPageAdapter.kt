package com.atria.myapplication.utils

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.atria.myapplication.AboutFragment
import com.atria.myapplication.ImagesFragment
import com.atria.myapplication.VideoFragment

class SectionsPageAdapter(
    val context : Context,
    val fragmentManager: FragmentManager,
    val behavior:Int
) :FragmentStatePagerAdapter(fragmentManager,behavior){

    companion object{
        val array = arrayOf("About Me","Portfolio","Videos")
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return array[position]
    }

    override fun getCount(): Int {
      return 3;
    }

    override fun getItem(position: Int): Fragment {
        if(position == 1){
            return ImagesFragment()
        }else if (position == 2){
            return VideoFragment()
        }
        return AboutFragment()
    }


}