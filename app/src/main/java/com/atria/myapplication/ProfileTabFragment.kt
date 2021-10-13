package com.atria.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.atria.myapplication.databinding.FragmentProfileTabBinding
import com.atria.myapplication.utils.SectionsPageAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore

class ProfileTabFragment : Fragment(), Thread.UncaughtExceptionHandler {

    private lateinit var profileTabFragmentBinding : FragmentProfileTabBinding

    var currentPage = 0;

    companion object {
        private const val TAG = "ProfileTabFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileTabFragmentBinding = FragmentProfileTabBinding.inflate(inflater,container,false)
        return profileTabFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(this)

        val sectionsPagerAdapter = SectionsPageAdapter(requireContext(),  childFragmentManager,FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        profileTabFragmentBinding.viewPager.adapter = sectionsPagerAdapter
        profileTabFragmentBinding.tabs.setupWithViewPager(profileTabFragmentBinding.viewPager)
        profileTabFragmentBinding.viewPager.offscreenPageLimit = 2

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: ")
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