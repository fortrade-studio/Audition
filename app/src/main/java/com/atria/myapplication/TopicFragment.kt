package com.atria.myapplication

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.atria.myapplication.adapter.CategoryCardAdapter
import com.atria.myapplication.databinding.FragmentTopicBinding
import com.atria.myapplication.viewModel.profession.ProfessionFragmentViewModel
import com.atria.myapplication.viewModel.profession.ProfessionFragmentViewModelFactory
import com.atria.myapplication.viewModel.topic.TopicFragmentViewModel
import com.atria.myapplication.viewModel.topic.TopicFragmentViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore


class TopicFragment : Fragment(), Thread.UncaughtExceptionHandler {

    private lateinit var fragmentTopicBinding: FragmentTopicBinding
    private lateinit var animatedDrawable: AnimatedVectorDrawable
    private lateinit var topicFragmentViewModel : TopicFragmentViewModel
    private val scrollStateLive = MutableLiveData<State>()
    private var previousPage:Int = 0

    private val MOVIES = "AUDITIONING FOR MOVIES"
    private val TELEVISION = "AUDITIONING FOR TELEVISION"
    private val MUSIC = "AUDITIONING FOR MUSIC"

    private lateinit var intent : Intent
    private var value : String = "MOVIES"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentTopicBinding = FragmentTopicBinding.inflate(inflater, container, false)
        return fragmentTopicBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(this)
        topicFragmentViewModel = ViewModelProvider(this, TopicFragmentViewModelFactory(requireActivity())).get(
            TopicFragmentViewModel::class.java
        )


        val topics = listOf(R.raw.movies, R.raw.television, R.raw.musiclofi)
        fragmentTopicBinding.cardPagerView.adapter =
            CategoryCardAdapter(topics, requireContext(), 3)

        fragmentTopicBinding.itemSelected.setOnClickListener {
            // this is where we navigate to the home activity
            topicFragmentViewModel.uploadAuditionType(value) {
                intent = Intent(requireContext(), HomeActivity::class.java)
                requireContext().startActivity(intent)
            }
        }

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
                                    value = "MOVIES"
                                }
                                1 -> {
                                    itemSelected.text = TELEVISION
                                    value = "TELEVISION"
                                }
                                else -> {
                                    itemSelected.text = MUSIC
                                    value = "MUSIC"
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

    override fun uncaughtException(t: Thread, e: Throwable) {
        FirebaseFirestore.getInstance()
            .collection("Error")
            .document(this::class.java.simpleName)
            .collection(this::class.java.simpleName.toUpperCase())
            .document(e.localizedMessage)
            .set(mapOf(Pair("value",e.stackTraceToString())))
            .addOnSuccessListener { Log.i("here", "uncaughtException: reported")}
    }

}