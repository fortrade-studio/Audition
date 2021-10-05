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
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.atria.myapplication.adapter.HomeAdapter
import com.atria.myapplication.databinding.FragmentHomeBinding
import com.atria.myapplication.diffutils.VideoData
import com.atria.myapplication.notification.NotificationData
import com.atria.myapplication.notification.PushNotification
import com.atria.myapplication.notification.RetrofitInstance
import com.atria.myapplication.service.NotificationFirebaseService
import com.atria.myapplication.utils.NumberToUniqueStringGenerator.Companion.userUniqueString
import com.atria.myapplication.viewModel.home.HomeParentViewModel
import com.atria.myapplication.viewModel.home.HomeParentViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeParentViewModel = ViewModelProvider(
            this,
            HomeParentViewModelFactory()
        ).get(HomeParentViewModel::class.java)

        NotificationFirebaseService.sharedPreference =
            requireContext().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener {
            NotificationFirebaseService.token = it.token
        }
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + userUniqueString())
            .addOnSuccessListener {}
            .addOnFailureListener {
                Log.e(TAG, "onViewCreated: ", it)
                Toast.makeText(
                        requireContext(),
                "Notification Pipe Unable to Set!",
                Toast.LENGTH_LONG
                ).show()
            }


//        val adapter = HomeAdapter(requireContext(),requireView(),arrayListOf(VideoData("https://www.rmp-streaming.com/media/big-buck-bunny-360p.mp4",0,"+919548955457"),
//            VideoData("https://www.rmp-streaming.com/media/big-buck-bunny-360p.mp4",0,"aryeahtyagi"),VideoData("https://www.rmp-streaming.com/media/big-buck-bunny-360p.mp4",0,"+919548955457")))
        val adapter = HomeAdapter(requireContext(),requireView(),ArrayList())
        homeFragmentBinding.viewPager2.adapter = adapter



        homeParentViewModel.getVideos{
           adapter.updateList(it.values.toList())
        }


    }


}