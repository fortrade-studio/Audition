package com.atria.myapplication.viewModel.images

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ImagesFragmentViewModelFactory(
    val context: Context,
    val view : View
) :ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ImagesFragmentViewModel(context, view) as T
    }

}