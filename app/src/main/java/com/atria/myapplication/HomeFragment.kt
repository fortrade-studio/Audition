package com.atria.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.atria.myapplication.adapter.HomeAdapter
import com.atria.myapplication.databinding.FragmentHomeBinding
import com.atria.myapplication.diffutils.VideoData
import com.atria.myapplication.notification.NotificationData
import com.atria.myapplication.notification.PushNotification
import com.atria.myapplication.notification.RetrofitInstance
import com.atria.myapplication.service.NotificationFirebaseService
import com.atria.myapplication.utils.NumberToUniqueStringGenerator.Companion.uniqueToNumber
import com.atria.myapplication.utils.NumberToUniqueStringGenerator.Companion.userUniqueString
import com.atria.myapplication.viewModel.home.HomeParentViewModel
import com.atria.myapplication.viewModel.home.HomeParentViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


class HomeFragment : Fragment() {

    private lateinit var homeFragmentBinding: FragmentHomeBinding
    private lateinit var homeParentViewModel: HomeParentViewModel
    private val positionLiveData  = MutableLiveData<Int>(0)

    companion object {
        private const val TAG = "HomeFragment"
        private val num = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeFragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return homeFragmentBinding.root
    }

    private fun getVideoId(link:String):String{
        return link.substringAfter("?","-1").substringBefore(",auth.url")
    }

    private fun getLink(link: String):String{
        return link.substringAfter(",auth.url=").trim()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var id:String = ""
        var link :String = ""

        val data = requireActivity().intent.data
        if(data!=null){
            id = uniqueToNumber(getVideoId(data.toString()))
            link = getLink(data.toString())
            val ph = id.dropLast(1)
            Log.i(TAG, "onViewCreated: $id & $link $ph")
        }else{
            Log.i(TAG, "onViewCreated: not found link")
        }
        homeParentViewModel = ViewModelProvider(
            this,
            HomeParentViewModelFactory()
        ).get(HomeParentViewModel::class.java)

        val list :List<VideoData> = if(data==null) {
            listOf(
                VideoData(
                    "https://www.rmp-streaming.com/media/big-buck-bunny-360p.mp4\n",
                    userid = "+919548955457",
                    uvid = "+9195489554572"
                ),
            )

        }else{
            Log.i(TAG, "onViewCreated: $link")
            listOf(
                VideoData(
                    link,userid = id.dropLast(1) , uvid = id
                )
            )
        }
        val adapter = HomeAdapter(requireContext(),requireView(),
           ArrayList(list)
        )
        homeFragmentBinding.viewPager2.adapter = adapter
        homeFragmentBinding.viewPager2.offscreenPageLimit = 5

//
//        homeParentViewModel.getVideos{
//             adapter.updateList(it.values.toList())
//        }

        homeFragmentBinding.viewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                Log.i(TAG, "onPageScrolled: ")
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val recycler = homeFragmentBinding.viewPager2[0] as RecyclerView
                val constraintLayout = recycler[position] as ConstraintLayout
                val video = constraintLayout[0] as VideoView
                video.start()

                if(position!=0){
                    val previousConstraint = recycler[position-1] as ConstraintLayout
                    val preVideo = previousConstraint[0] as VideoView
                    preVideo.seekTo(0)
                    preVideo.pause()
                }

                if(position != recycler.childCount.minus(1)){
                    val nextConstraint = recycler[position+1] as ConstraintLayout
                    val nextVideo = nextConstraint[0] as VideoView
                    nextVideo.seekTo(0)
                    nextVideo.pause()
                }

            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                Log.i(TAG, "onPageScrollStateChanged: ")
            }
        })


    }


}