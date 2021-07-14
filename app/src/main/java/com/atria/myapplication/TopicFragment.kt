package com.atria.myapplication

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.ViewPager
import com.atria.myapplication.adapter.CategoryCardAdapter
import com.atria.myapplication.databinding.FragmentTopicBinding


class TopicFragment : Fragment() {

    private lateinit var fragmentTopicBinding: FragmentTopicBinding
    private lateinit var animatedDrawable: AnimatedVectorDrawable
    private val scrollStateLive = MutableLiveData<State>()
    private var previousPage:Int = 0

    private val MOVIES = "AUDITIONING FOR MOVIES"
    private val TELEVISION = "AUDITIONING FOR TELEVISION"
    private val MUSIC = "AUDITIONING FOR MUSIC"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentTopicBinding = FragmentTopicBinding.inflate(inflater, container, false)
        return fragmentTopicBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topics = listOf(R.raw.movies, R.raw.television, R.raw.musiclofi)
        fragmentTopicBinding.cardPagerView.adapter =
            CategoryCardAdapter(topics, requireContext(), 3)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            animatedDrawable = context?.getDrawable(R.drawable.firsttomid) as AnimatedVectorDrawable
            fragmentTopicBinding.pagerView.setImageDrawable(animatedDrawable)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            scrollStateLive.observe(viewLifecycleOwner) {
                it.updateAnimated()
            }
        }

        with(fragmentTopicBinding) {
            cardPagerView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
//                    TODO("Not yet implemented")
                }

                override fun onPageSelected(position: Int) {
                    scrollStateLive.postValue(State(previousPage,position))
                    previousPage=position

                    itemSelected
                        .animate()
                        .alpha(0.5f)
                        .withEndAction {
                            when (position) {
                                0 -> {
                                    itemSelected.text = MOVIES
                                }
                                1 -> {
                                    itemSelected.text = TELEVISION
                                }
                                else -> {
                                    itemSelected.text = MUSIC
                                }
                            }
                            itemSelected.animate().alpha(1f).start()
                        }
                    
                }

                override fun onPageScrollStateChanged(state: Int) {
//                    TODO("Not yet implemented")
                }

            })
        }

    }

    private data class State(
        val previous: Int,
        val current: Int
    )

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun State.updateAnimated() {
        if (previous == 0 && current == 1) {
            animatedDrawable = context?.getDrawable(R.drawable.firsttomid) as AnimatedVectorDrawable
            fragmentTopicBinding.pagerView.setImageDrawable(animatedDrawable)
            animatedDrawable.start()
        } else if (previous == 1 && current == 2) {
            animatedDrawable = context?.getDrawable(R.drawable.midtolast) as AnimatedVectorDrawable
            fragmentTopicBinding.pagerView.setImageDrawable(animatedDrawable)
            animatedDrawable.start()
        } else if (previous == 2 && current == 1) {
            animatedDrawable = context?.getDrawable(R.drawable.lasttomid) as AnimatedVectorDrawable
            fragmentTopicBinding.pagerView.setImageDrawable(animatedDrawable)
            animatedDrawable.start()
        } else if (previous == 1 && current == 0) {
            animatedDrawable = context?.getDrawable(R.drawable.midtofirst) as AnimatedVectorDrawable
            fragmentTopicBinding.pagerView.setImageDrawable(animatedDrawable)
            animatedDrawable.start()
        }
        
    }

}