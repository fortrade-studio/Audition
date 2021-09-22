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
import com.atria.myapplication.databinding.FragmentHomeBinding
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


class HomeFragment : Fragment() , NavigationView.OnNavigationItemSelectedListener{

    private lateinit var homeFragmentBinding: FragmentHomeBinding
    private lateinit var homeParentViewModel : HomeParentViewModel
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
        homeParentViewModel = ViewModelProvider(this,HomeParentViewModelFactory()).get(HomeParentViewModel::class.java)

        NotificationFirebaseService.sharedPreference = requireContext().getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
        FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener {
            NotificationFirebaseService.token = it.token
        }
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+userUniqueString()).addOnSuccessListener {}
            .addOnFailureListener {
                Log.e(TAG, "onViewCreated: ",it )
                Toast.makeText(requireContext(),"Notification Pipe Unable to Set!",Toast.LENGTH_LONG).show()
            }

        homeFragmentBinding.hamburger.setOnClickListener {
            homeFragmentBinding.drawerLayout.openDrawer(GravityCompat.START)
        }

        homeFragmentBinding.navView.setNavigationItemSelectedListener(this)

        val headerView = homeFragmentBinding.navView.getHeaderView(0)
        val profileView = headerView.findViewById<CircleImageView>(R.id.profileImageView)
        val nameTextView = headerView.findViewById<TextView>(R.id.nameTextView)

        val exitView = headerView.findViewById<ImageView>(R.id.exitImageView)
        exitView.setOnClickListener {
            if (homeFragmentBinding.drawerLayout.isOpen){
                homeFragmentBinding.drawerLayout.close()
            }
        }
        homeParentViewModel.getUserProfileAndName { image, name ->
            nameTextView.text = name
            Glide.with(this)
                .load(image)
                .into(profileView)
        }

        homeFragmentBinding.searchEditText.addTextChangedListener {
            if(mainHomeFragment?.callback?.value != 1 ) {
                mainHomeFragment?.callback?.postValue(1)
                homeFragmentBinding.hamburger.setImageResource(R.drawable.ic_arrow)
                homeFragmentBinding.hamburger.setOnClickListener {
                    homeFragmentBinding.searchEditText.setText("")
                    homeFragmentBinding.searchEditText.clearFocus()
                    val imm: InputMethodManager? =
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)
                    Constants.backCallback.postValue(-1)
                    homeFragmentBinding.hamburger.setImageResource(R.drawable.ic_baseline_dehaze_24)
                    Constants.searchStringLiveData.value = ""
                }
            }
            searchStringLiveData.postValue(it.toString())
        }

        val bundle = Bundle()
        homeToProfileCallback.observe(viewLifecycleOwner){
            if(it.isNotEmpty()) {
                bundle.putString("ph", it)
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment, bundle)
                homeToProfileCallback.value = ""
            }
        }
        homeFragmentBinding.profileImageView.setOnClickListener {
            homeParentViewModel.sendNotification(
                // todo: Change this cause notification is send to this user's number
                PushNotification(NotificationData("","You got a follower."), "/topics/${userUniqueString()}")
            )
        }

    }

    private fun animateBell(){
        homeFragmentBinding.profileImageView.animate()
            .rotation(45f)
            .setDuration(190L)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                homeFragmentBinding.profileImageView.animate()
                    .rotation(-45f)
                    .setInterpolator(AccelerateInterpolator())
                    .withEndAction {
                        homeFragmentBinding.profileImageView.animate()
                            .rotation(0f)
                            .setInterpolator(AccelerateInterpolator())
                            .start()
                    }
                    .start()
            }
            .start()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.profileDrawerView ->{
                homeToProfileCallback.postValue(num)
            }
            R.id.dashboardDrawerView->{

            }
            R.id.savedDrawerView ->{

            }
            R.id.SettingDrawerView ->{

            }
        }
        return true;
    }




}