package com.atria.myapplication.viewModel.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SearchFragmentViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchFragmentViewModel() as T
    }

}