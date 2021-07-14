package com.atria.myapplication.viewModel.register

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LoginBackFragmentViewModelFactory(
    val context:Context,
    val view :View
) :ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginBackFragmentViewModel(context, view) as T
    }
}