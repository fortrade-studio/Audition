package com.atria.myapplication.viewModel.topic

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TopicFragmentViewModelFactory(
    val activity: Activity
):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TopicFragmentViewModel(activity) as T
    }

}