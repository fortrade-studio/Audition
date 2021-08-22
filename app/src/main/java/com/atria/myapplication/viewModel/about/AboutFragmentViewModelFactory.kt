package com.atria.myapplication.viewModel.about

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AboutFragmentViewModelFactory(
    val context: Context,
    val view: View
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AboutFragmentViewModel(context, view) as T
    }
}