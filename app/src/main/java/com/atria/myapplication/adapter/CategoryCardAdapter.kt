package com.atria.myapplication.adapter

import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.airbnb.lottie.LottieAnimationView
import com.atria.myapplication.R
import java.util.zip.Inflater

class CategoryCardAdapter(
    val list:List<Int>,
    val context : Context,
    val size:Int=2
) : PagerAdapter() {

    override fun getCount(): Int {
        return  size;
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view = LayoutInflater.from(context).inflate(R.layout.category_cards,container,false)
        container.addView(view,position)
        val animationView = view.findViewById<LottieAnimationView>(R.id.animationView)
        animationView.setAnimation(list[position])
        return view
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}