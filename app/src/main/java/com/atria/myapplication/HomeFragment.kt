package com.atria.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.Gravity.START
import android.view.animation.AccelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.GravityCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.atria.myapplication.Constants.homeToProfileCallback
import com.atria.myapplication.Constants.mainHomeFragment
import com.atria.myapplication.Constants.searchStringLiveData
import com.atria.myapplication.adapter.HomeAdapter
import com.atria.myapplication.databinding.FragmentHomeBinding
import com.atria.myapplication.diffutils.VideoData
import com.atria.myapplication.notification.NotificationData
import com.atria.myapplication.notification.PushNotification
import com.atria.myapplication.service.NotificationFirebaseService
import com.atria.myapplication.ui.main.MainActivity
import com.atria.myapplication.utils.NumberToUniqueStringGenerator.Companion.userUniqueString
import com.atria.myapplication.viewModel.home.HomeParentViewModel
import com.atria.myapplication.viewModel.home.HomeParentViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.bundle.BundleLoader
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class HomeFragment : Fragment() {

    private lateinit var homeFragmentBinding: FragmentHomeBinding
    private lateinit var homeParentViewModel: HomeParentViewModel

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

        val adapter = HomeAdapter(ArrayList())
        homeFragmentBinding.viewPager2.adapter = adapter

        homeParentViewModel.getVideos{
           adapter.updateList(it.values.toList())
        }

    }


}