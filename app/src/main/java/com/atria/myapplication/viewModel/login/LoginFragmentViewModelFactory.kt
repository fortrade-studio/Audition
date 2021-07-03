package com.atria.myapplication.viewModel.login

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LoginFragmentViewModelFactory(
    private val context : Context,
    private val view : View
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginFragmentViewModel(context , view)  as T
    }

}