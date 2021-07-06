package com.atria.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atria.myapplication.adapter.CategoryCardAdapter
import com.atria.myapplication.databinding.FragmentTopicBinding


class TopicFragment : Fragment() {

    private lateinit var fragmentTopicBinding : FragmentTopicBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentTopicBinding = FragmentTopicBinding.inflate(inflater,container,false)
        return fragmentTopicBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topics = listOf(R.raw.movies,R.raw.television,R.raw.musiclofi)
        fragmentTopicBinding.cardPagerView.adapter = CategoryCardAdapter(topics,requireContext(),3)

    }
}