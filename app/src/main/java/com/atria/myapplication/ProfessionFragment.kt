package com.atria.myapplication

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.atria.myapplication.adapter.CategoryCardAdapter
import com.atria.myapplication.databinding.FragmentProfessionBinding


class ProfessionFragment : Fragment() {
    
    
    private lateinit var professionFragmentBinding : FragmentProfessionBinding
    private lateinit var animatedVectorDrawable : AnimatedVectorDrawable


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        professionFragmentBinding= FragmentProfessionBinding.inflate(inflater,container,false)
        return professionFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        professionFragmentBinding.itemSelected.setOnClickListener {
            findNavController().navigate(R.id.action_professionFragment_to_topicFragment)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            animatedVectorDrawable = context?.getDrawable(R.drawable.avd_anim) as AnimatedVectorDrawable
            professionFragmentBinding.pagerView.setImageDrawable(animatedVectorDrawable)

        }else{
            professionFragmentBinding.pagerView.setBackgroundResource(R.drawable.ic___swipe__)
        }

        val categoryCardAdapter =
            CategoryCardAdapter(listOf(R.raw.artist, R.raw.director), requireContext())


        professionFragmentBinding.cardPagerView.adapter = categoryCardAdapter
        professionFragmentBinding.cardPagerView.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if(position==1) {
                        animatedVectorDrawable.start()
                        animatedVectorDrawable = context?.getDrawable(R.drawable.avd_anim_rev) as AnimatedVectorDrawable
                        professionFragmentBinding.pagerView.setImageDrawable(animatedVectorDrawable)
                    }else {
                        animatedVectorDrawable.start()
                        animatedVectorDrawable = context?.getDrawable(R.drawable.avd_anim) as AnimatedVectorDrawable
                        professionFragmentBinding.pagerView.setImageDrawable(animatedVectorDrawable)
                    }
                }

                if(position==0){
                    professionFragmentBinding.itemSelected.setOnClickListener {
                        findNavController().navigate(R.id.action_professionFragment_to_topicFragment)
                    }
                }else{
                    professionFragmentBinding.itemSelected.setOnClickListener {  }
                }

                professionFragmentBinding.itemSelected
                    .animate()
                    .alpha(0.5f)
                    .withEndAction {
                        if (position == 0) {
                            professionFragmentBinding.itemSelected.text = "I AM AN ARTIST"
                        }else{
                            professionFragmentBinding.itemSelected.text = "I AM A DIRECTOR"
                        }
                        professionFragmentBinding.itemSelected.animate().alpha(1f).start()
                    }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })


    }
    

}