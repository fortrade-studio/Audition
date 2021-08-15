package com.atria.myapplication.viewModel.profile

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProfileFragmentViewModelFactory(
    val context :  Context,
    val view:View
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileFragmentViewModel(context,view) as T
    }
}