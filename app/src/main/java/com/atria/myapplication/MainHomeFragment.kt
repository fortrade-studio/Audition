package com.atria.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atria.myapplication.adapter.ActiveAuditionAdapter
import com.atria.myapplication.adapter.AuditionAdapter
import com.atria.myapplication.databinding.FragmentMainHomeBinding
import com.atria.myapplication.viewModel.home.HomeFragmentViewModel
import com.atria.myapplication.viewModel.home.HomeFragmentViewModelFactory
import com.google.api.Distribution


class MainHomeFragment : Fragment() {

    private lateinit var mainHomeFragmentBinding : FragmentMainHomeBinding
    private lateinit var homeFragmentViewModel: HomeFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainHomeFragmentBinding = FragmentMainHomeBinding.inflate(inflater,container,false)
        return mainHomeFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeFragmentViewModel  = ViewModelProvider(this,HomeFragmentViewModelFactory(requireView(),requireContext())).get(HomeFragmentViewModel::class.java)

        mainHomeFragmentBinding.newestOpeningRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        homeFragmentViewModel.getNewestOpenings {
            mainHomeFragmentBinding.newestOpeningRecyclerView.adapter = AuditionAdapter(it,this)
        }

        mainHomeFragmentBinding.activelyHiringRecyclerView.isNestedScrollingEnabled = false
        mainHomeFragmentBinding.activelyHiringRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        homeFragmentViewModel.getActivelyOpening {
            mainHomeFragmentBinding.activelyHiringRecyclerView.adapter =ActiveAuditionAdapter(it, this)
        }
    }

}