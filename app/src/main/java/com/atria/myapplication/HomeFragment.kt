package com.atria.myapplication

import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.atria.myapplication.adapter.TopAuditionAdapter
import com.atria.myapplication.databinding.FragmentHomeBinding
import com.atria.myapplication.viewModel.home.HomeFragmentViewModel
import com.atria.myapplication.viewModel.home.HomeFragmentViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL


class HomeFragment : Fragment() {

    private lateinit var homeFragmentBinding: FragmentHomeBinding
    private lateinit var homeFragmentViewModel: HomeFragmentViewModel

    companion object {
        private const val TAG = "HomeFragment"
        private val main = CoroutineScope(Dispatchers.Main)
        private val io = CoroutineScope(Dispatchers.IO)
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

        homeFragmentViewModel = ViewModelProvider(
            this, HomeFragmentViewModelFactory(
                requireView(),
                requireContext()
            )
        ).get(HomeFragmentViewModel::class.java)

        homeFragmentBinding.topRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        // we need to first load the top scorer image and then we need to load his/her name
        homeFragmentViewModel.liveTopScorer.observe(viewLifecycleOwner) {
            homeFragmentBinding.topRecyclerView.adapter = TopAuditionAdapter(it)
        }

        homeFragmentViewModel.getTopAuditionData()


    }

    private fun loadImage(link: String, username: String) {

    }

}