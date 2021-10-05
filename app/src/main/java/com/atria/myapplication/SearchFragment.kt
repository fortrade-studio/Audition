package com.atria.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atria.myapplication.Constants.searchStringLiveData
import com.atria.myapplication.adapter.SearchAdapter
import com.atria.myapplication.databinding.FragmentSearchBinding
import com.atria.myapplication.utils.SectionsPageAdapter
import com.atria.myapplication.viewModel.search.SearchFragmentViewModel
import com.atria.myapplication.viewModel.search.SearchFragmentViewModelFactory


class SearchFragment : Fragment() {

    private lateinit var searchFragmentBinding: FragmentSearchBinding
    private lateinit var searchFragmentViewModel: SearchFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchFragmentBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return searchFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchFragmentViewModel = ViewModelProvider(
            this,
            SearchFragmentViewModelFactory()
        ).get(SearchFragmentViewModel::class.java)
        searchFragmentBinding.searchResultRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )


        Constants.homeToProfileCallback.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                val bundle = Bundle().apply {
                    this.putString("ph", it.toString())
                }
                Constants.homeToProfileCallback.value = ""
                findNavController().navigate(R.id.action_searchFragment_to_profileFragment, bundle)
            }
        }

        val searchAdapter =
            SearchAdapter(ArrayList(), this)
        searchFragmentBinding.searchResultRecyclerView.adapter = searchAdapter

        searchFragmentBinding.searchInputEditText.addTextChangedListener {
            if (!it.isNullOrEmpty()) {
                searchStringLiveData.postValue(it.toString())
            }
        }

        searchFragmentBinding.searchButton.setOnClickListener {

        }

        searchStringLiveData.observe(viewLifecycleOwner) {
            searchFragmentViewModel.search(it) { l ->
                searchAdapter.updateArrayList(l as ArrayList<SearchFragmentViewModel.SearchUser>)
            }
        }

    }

}