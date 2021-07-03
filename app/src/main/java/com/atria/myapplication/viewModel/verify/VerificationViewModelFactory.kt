package com.atria.myapplication.viewModel.verify

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VerificationViewModelFactory(
    val context : Context,
    val view : View
) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VerificationFragmentViewModel(context,view) as T
    }

}