package com.atria.myapplication


import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.atria.myapplication.Constants.big
import com.atria.myapplication.Constants.big_link
import com.atria.myapplication.Constants.circular_link
import com.atria.myapplication.databinding.FragmentProfileBinding
import com.atria.myapplication.viewModel.profile.ProfileFragmentViewModel
import com.atria.myapplication.viewModel.profile.ProfileFragmentViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() , Thread.UncaughtExceptionHandler{

    private lateinit var profileFragmentBinding: FragmentProfileBinding
    private lateinit var profileViewModel: ProfileFragmentViewModel

    private var isFollowing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        profileFragmentBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return profileFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler(this)
        // we get the id of the user
        val id: String = arguments?.getString("ph") ?: auth?.phoneNumber!!
        Constants.profile_id = id


        profileViewModel = ViewModelProvider(
            this, ProfileFragmentViewModelFactory(
                requireContext(),
                requireView()
            )
        ).get(ProfileFragmentViewModel::class.java)



        profileFragmentBinding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }

        profileViewModel.checkIfFollow(id)


        if (auth?.phoneNumber == id) {
            profileFragmentBinding.followButton.text = "Edit Profile"
            profileFragmentBinding.followButton.setBackgroundResource(R.drawable.button_rounded_hollow)
            profileFragmentBinding.hamburgerImageView.visibility = View.VISIBLE
        } else {
            profileViewModel.followLive.observe(viewLifecycleOwner) {
                if (it) {
                    isFollowing = true
                    profileFragmentBinding.followButton.text = "UNFOLLOW"
                    profileFragmentBinding.followButton.setBackgroundResource(R.drawable.button_rounded_hollow)
                } else {
                    isFollowing = false
                    profileFragmentBinding.followButton.text = "FOLLOW"
                    profileFragmentBinding.followButton.setBackgroundResource(R.drawable.button_rounded_rect)
                }
            }
        }
        profileViewModel.onFollowFetch.observe(viewLifecycleOwner) {
            profileFragmentBinding.followTextView.text = it.toString()
        }

        // big "https://firebasestorage.googleapis.com/v0/b/audition-15207.appspot.com/o/default%2Fbackground.jpg?alt=media&token=f80b49c5-bc9b-4e88-b7b2-e60fc372e8f6",
        // cir "https://firebasestorage.googleapis.com/v0/b/audition-15207.appspot.com/o/default%2Fprofile.jpg?alt=media&token=9c73401c-6ac1-4826-932e-21c07d43b1a7",

        profileViewModel.getUserData(id) { v ->
            var biglink = ""
            var cirlink = ""
            if (v.big.isNotEmpty() && v.big != "not set") {
                big_link = v.big
                biglink = v.big
            } else {
                biglink =
                    "https://firebasestorage.googleapis.com/v0/b/audition-15207.appspot.com/o/default%2Fbackground.jpg?alt=media&token=f80b49c5-bc9b-4e88-b7b2-e60fc372e8f6"
            }
            if (v.circular.isNotEmpty() && v.circular != "not set") {
                circular_link = v.circular
                cirlink = v.circular
            } else {
                cirlink = circular_link
            }
            Glide.with(requireContext())
                .load(biglink)
                .into(profileFragmentBinding.bigScreenImageView)

            Glide.with(requireContext())
                .load(cirlink)
                .into(profileFragmentBinding.cicularProfileView)

            if (auth?.phoneNumber != id) {
                profileFragmentBinding.followButton.setOnClickListener {
                    if (isFollowing) {
                        profileViewModel.unFollowUser(id) {
                            isFollowing = false
                            profileViewModel.followLive.postValue(false)
                        }
                    } else {
                        profileViewModel.followUser(id, v.username,v.circular,v.name) {
                            isFollowing = true
                            profileViewModel.followLive.postValue(true)
                        }
                    }
                }
            } else {
                profileFragmentBinding.followButton.setOnClickListener {
                    findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
                }
            }
            profileFragmentBinding.nameTextView.text = v.name
            profileFragmentBinding.usernameTextView.text = "@"+v.username
            profileFragmentBinding.summaryTextView.text = v.summary
            profileFragmentBinding.followTextView.text = v.follower.toString()
            profileViewModel.getFollowing(id) {
                 profileFragmentBinding.followingTextView.text = it.toString()
            }

        }

        if (auth?.phoneNumber == id) {
            profileFragmentBinding.gridLayout.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_followingFragment)
            }
        }

    }


    companion object {
        private const val TAG = "ProfileFragment"
        private val auth = FirebaseAuth.getInstance().currentUser
    }
    override fun uncaughtException(t: Thread, e: Throwable) {
        FirebaseFirestore.getInstance()
            .collection("Error")
            .document(this::class.java.simpleName)
            .collection(this::class.java.simpleName.toUpperCase())
            .document(e.localizedMessage)
            .set(mapOf(Pair("value",e.stackTraceToString())))
            .addOnSuccessListener { Log.i(TAG, "uncaughtException: reported")}
    }




}