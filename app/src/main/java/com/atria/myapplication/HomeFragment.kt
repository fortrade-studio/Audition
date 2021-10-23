package com.atria.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.atria.myapplication.Constants.isHome
import com.atria.myapplication.adapter.HomeAdapter
import com.atria.myapplication.databinding.FragmentHomeBinding
import com.atria.myapplication.diffutils.ParserVideos
import com.atria.myapplication.diffutils.VideoData
import com.atria.myapplication.utils.NumberToUniqueStringGenerator.Companion.uniqueToNumber
import com.atria.myapplication.utils.ViewUtils
import com.atria.myapplication.viewModel.home.HomeParentViewModel
import com.atria.myapplication.viewModel.home.HomeParentViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable


class HomeFragment : Fragment(), Thread.UncaughtExceptionHandler {

    private lateinit var homeFragmentBinding: FragmentHomeBinding
    private lateinit var homeParentViewModel: HomeParentViewModel
    private val positionLiveData  = MutableLiveData<Int>(0)
    private lateinit var adapter:HomeAdapter
    private var extra:Serializable? = null

    companion object {
        private const val TAG = "HomeFragment"
        private val num = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()
        private const val MY_PREFS_NAME = "User"
        private const val logged = "loggedIn"
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
        isHome = true
        Thread.setDefaultUncaughtExceptionHandler(this)
        var id:String = ""
        var link :String = ""
        var ph:String = ""

        storeInCache()

        val data = requireActivity().intent.data
        extra = arguments?.getSerializable("videos")

        if(data!=null){
            id = uniqueToNumber(getVideoId(data.toString()))
            link = getLink(data.toString())
            ph = id.dropLast(1)
            Log.i(TAG, "onViewCreated: $id & $link $ph")
        }else{
            Log.i(TAG, "onViewCreated: not found link")
        }
        homeParentViewModel = ViewModelProvider(
            this,
            HomeParentViewModelFactory()
        ).get(HomeParentViewModel::class.java)

      adapter  = when {
          data != null -> {
              HomeAdapter(
                  requireContext(), requireView(),
                  arrayListOf(VideoData(link,likes = 0,ph,id)),
                  requireActivity(),viewLifecycleOwner
              )
          }
          extra!=null -> {
              val videos = extra as ParserVideos
              HomeAdapter(
                  requireContext(), requireView(),
                  linkToVideos(videos,videos.pos),
                  requireActivity(),viewLifecycleOwner,true
              )
          }
          else -> {
              HomeAdapter(
                  requireContext(),requireView(), arrayListOf(),
                  requireActivity(),viewLifecycleOwner
              )
          }
      }
        homeFragmentBinding.viewPager2.adapter = adapter
        if(extra == null) {
            homeFragmentBinding.viewPager2.offscreenPageLimit = 5
            homeParentViewModel.getVideos {
                adapter.updateList(it.values.toList().shuffled())
            }
        }else{
            val  parser = extra as ParserVideos
            Log.i(TAG, "onViewCreated: ${parser.pos} ${parser.list.toString()}")
            ViewUtils.waitForLayout(requireView()){
                isHome = false
                homeFragmentBinding.viewPager2.setCurrentItem(parser.pos,true)
            }
        }

        homeFragmentBinding.viewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (extra == null) {
                    val recycler = homeFragmentBinding.viewPager2[0] as RecyclerView
                    val constraintLayout = recycler[position] as ConstraintLayout
                    val video = constraintLayout[0] as VideoView
                    video.start()

                    if (position != 0) {
                        val previousConstraint = recycler[position - 1] as ConstraintLayout
                        val preVideo = previousConstraint[0] as VideoView
                        preVideo.seekTo(0)
                        preVideo.pause()
                    }

                    if (position != recycler.childCount.minus(1)) {
                        val nextConstraint = recycler[position + 1] as ConstraintLayout
                        val nextVideo = nextConstraint[0] as VideoView
                        nextVideo.seekTo(0)
                        nextVideo.pause()
                    }
                }

            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })


    }

    override fun onResume() {
        super.onResume()
        isHome = true
        if (extra == null) {
            homeParentViewModel.getVideos {
                adapter.updateList(it.values.toList().shuffled())
            }
        }
    }

    private fun MutableList<String>.swapFirst(secIndex:Int, value:String){
        val first = this[0]
        this.add(0,value)
        this.add(secIndex,first)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        FirebaseFirestore.getInstance()
            .collection("Error")
            .document(this::class.java.simpleName)
            .collection(this::class.java.simpleName.toUpperCase())
            .document(e.localizedMessage)
            .set(mapOf(Pair("value",e.stackTraceToString())))
            .addOnSuccessListener {
                Log.i(TAG, "uncaughtException: reported")
            }
    }

    private fun linkToVideos(arr:ParserVideos, pos:Int):ArrayList<VideoData>{
        val ar = ArrayList<VideoData>()
        var index =-1
        for (l in arr.list){
            ++index;
            ar.add(VideoData(l,0,arr.id,arr.id+index))
        }
        return ar
    }

    fun storeInCache(){
        val editor: SharedPreferences.Editor =
            requireActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putInt(logged, 10)
        editor.apply()
    }


    override fun onPause() {
        super.onPause()
        isHome = false
        extra=null
    }

    override fun onStop() {
        super.onStop()
        isHome = false
        extra=null
    }

    override fun onDestroy() {
        super.onDestroy()
        isHome = false
        extra=null
    }

}