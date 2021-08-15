package com.atria.myapplication.viewModel.video

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VideoFragmentViewModelFactory(
    val context: Context,
    val view:View
) :ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideoFragmentViewModel(context, view) as T
    }


}