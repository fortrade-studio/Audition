package com.atria.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.atria.myapplication.databinding.FragmentProfileTabBinding
import com.atria.myapplication.utils.SectionsPageAdapter
import com.google.android.material.tabs.TabLayout

class ProfileTabFragment : Fragment() {

    private lateinit var profileTabFragmentBinding : FragmentProfileTabBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileTabFragmentBinding = FragmentProfileTabBinding.inflate(inflater,container,false)
        return profileTabFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sectionsPagerAdapter = SectionsPageAdapter(requireContext(), requireActivity().supportFragmentManager )
        profileTabFragmentBinding.viewPager.adapter = sectionsPagerAdapter
        profileTabFragmentBinding.tabs.setupWithViewPager(profileTabFragmentBinding.viewPager)

    }

}