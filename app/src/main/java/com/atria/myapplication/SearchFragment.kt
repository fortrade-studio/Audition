package com.atria.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentStatePagerAdapter
import com.atria.myapplication.databinding.FragmentSearchBinding
import com.atria.myapplication.utils.SectionsPageAdapter


class SearchFragment : Fragment() {

    private lateinit var searchFragmentBinding : FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchFragmentBinding = FragmentSearchBinding.inflate(inflater,container,false)
        return searchFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val sectionsPagerAdapter = SectionsPageAdapter(requireContext(),  childFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        searchFragmentBinding.viewPager.adapter = sectionsPagerAdapter
        searchFragmentBinding.tabs.setupWithViewPager(searchFragmentBinding.viewPager)

    }

}