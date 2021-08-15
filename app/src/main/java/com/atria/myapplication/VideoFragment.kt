package com.atria.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.atria.myapplication.adapter.VideoAdapter
import com.atria.myapplication.databinding.FragmentVideoBinding
import com.atria.myapplication.viewModel.video.VideoFragmentViewModel
import com.atria.myapplication.viewModel.video.VideoFragmentViewModelFactory


class VideoFragment : Fragment() {

    private lateinit var videoFragmentBinding : FragmentVideoBinding
    private lateinit var videoFragmentViewModel : VideoFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        videoFragmentBinding = FragmentVideoBinding.inflate(inflater,container,false)
        return videoFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoFragmentViewModel = ViewModelProvider(this,VideoFragmentViewModelFactory(requireContext(),requireView())).get(VideoFragmentViewModel::class.java)
        videoFragmentBinding.videosRecyclerView.layoutManager = GridLayoutManager(requireContext(),3,GridLayoutManager.VERTICAL,false)

        videoFragmentViewModel.getVideos {
            videoFragmentBinding.videosRecyclerView.adapter = VideoAdapter(it,requireContext())
        }

    }

}