package com.atria.myapplication.viewModel.edit

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EditProfileViewModelFactory(
    val context : Context,
    val view : View
) :ViewModelProvider.Factory{


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditProfileViewModel(context, view) as T
    }

}