package com.atria.myapplication

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore


class FollowingFragment : Fragment() , Thread.UncaughtExceptionHandler{

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
        Thread.setDefaultUncaughtExceptionHandler(this)
        followingFragmentViewModel = ViewModelProvider(this,FollowingViewModelFactory()).get(FollowingViewModel::class.java)

        if(context != null) {
            followingViewBinding.recyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            followingFragmentViewModel.getFollowings {
                if(context !=null) {
                    followingViewBinding.recyclerView.adapter =
                        FollowAdapter(it, requireContext(), requireView())
                }
            }
        }


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