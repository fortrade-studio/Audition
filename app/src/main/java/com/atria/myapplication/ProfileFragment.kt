package com.atria.myapplication

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.atria.myapplication.databinding.FragmentProfileBinding
import com.atria.myapplication.viewModel.profile.ProfileFragmentViewModel
import com.atria.myapplication.viewModel.profile.ProfileFragmentViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL


class ProfileFragment : Fragment() {

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


        // we get the id of the user
        val id = arguments?.getString("ph") ?: "+919548955457"
        Constants.profile_id = id

        profileViewModel = ViewModelProvider(
            this, ProfileFragmentViewModelFactory(
                requireContext(),
                requireView()
            )
        ).get(ProfileFragmentViewModel::class.java)

        profileViewModel.checkIfFollow(id)

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


        profileViewModel.getUserData(id) { v ->
            CoroutineScope(Dispatchers.IO).launch {
                val url = URL(v.big)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                val url2 = URL(v.circular)
                val bmp2 = BitmapFactory.decodeStream(url2.openConnection().getInputStream())
                CoroutineScope(Dispatchers.Main).launch {

                    profileFragmentBinding.followButton.setOnClickListener {
                        if (isFollowing) {
                            profileViewModel.unFollowUser(id) {
                                isFollowing = false
                                profileViewModel.followLive.postValue(false)
                            }
                        } else {
                            profileViewModel.followUser(id, v.username) {
                                isFollowing = true
                                profileViewModel.followLive.postValue(true)
                            }
                        }
                    }

                    profileFragmentBinding.cicularProfileView.setImageBitmap(bmp2)
                    profileFragmentBinding.bigScreenImageView.setImageBitmap(bmp)
                    profileFragmentBinding.nameTextView.text = v.name
                    profileFragmentBinding.usernameTextView.text = v.username
                    profileFragmentBinding.summaryTextView.text = v.summary
                    profileFragmentBinding.followTextView.text = v.follower.toString()
                    profileFragmentBinding.followingTextView.text = v.following.toString()

                }
            }
        }

    }

    companion object {
        private const val TAG = "ProfileFragment"
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: jh")
    }

}