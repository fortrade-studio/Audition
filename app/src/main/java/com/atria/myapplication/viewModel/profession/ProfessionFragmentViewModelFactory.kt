package com.atria.myapplication.viewModel.profession

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProfessionFragmentViewModelFactory(
    val activity: Activity
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfessionFragmentViewModel(activity) as T
    }

}