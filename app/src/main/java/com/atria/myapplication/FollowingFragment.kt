package com.atria.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.atria.myapplication.adapter.FollowAdapter
import com.atria.myapplication.databinding.FragmentFollowingBinding
import com.atria.myapplication.viewModel.follow.FollowingViewModel
import com.atria.myapplication.viewModel.follow.FollowingViewModelFactory
import com.atria.myapplication.viewModel.profile.ProfileFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue


class FollowingFragment : Fragment() {

    private lateinit var followingFragmentViewModel : FollowingViewModel
    private lateinit var followingViewBinding : FragmentFollowingBinding

    companion object{
        private const val TAG = "FollowingFragment"
        private val auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        followingViewBinding = FragmentFollowingBinding.inflate(inflater,container,false)
        return followingViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        followingFragmentViewModel = ViewModelProvider(this,FollowingViewModelFactory()).get(FollowingViewModel::class.java)

        followingViewBinding.recyclerView.layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

        followingFragmentViewModel.getFollowings {
            followingViewBinding.recyclerView.adapter = FollowAdapter(it,requireContext(),requireView())
        }

    }

}