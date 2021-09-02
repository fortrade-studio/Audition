package com.atria.myapplication.viewModel.about

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.atria.myapplication.Constants
import com.atria.myapplication.Constants.profile_id
import com.atria.myapplication.Constants.user
import com.atria.myapplication.room.User
import com.google.firebase.firestore.FirebaseFirestore

class AboutFragmentViewModel(
    val context: Context,
    val view: View
) : ViewModel() {

    private var user: User? = null


    companion object {
        private val firebase = FirebaseFirestore.getInstance()
    }


    fun getPersonalData(onSuccess: (User) -> Unit) {
        firebase.collection(Constants.user)
            .document(profile_id)
            .get()
            .addOnSuccessListener {
                if (user != null) {
                    onSuccess(user as User)
                } else {
                    user = it.toObject(User::class.java)
                    if (user != null) {
                        onSuccess(user as User)
                        Constants.profile = user!!.eliminateNullOrNotSet()
                    }
                }
            }
    }

    fun User.eliminateNullOrNotSet(): User {
        val user : User = this.copy()
        if(this.summary == "not set" || this.summary.isEmpty() ){
            user.summary =""
        }
        if(this.email == "not set" || this.email.isEmpty()){
            user.email =""
        }
        if(this.country == "not set" || this.country.isEmpty()){
            user.country =""
        }
        if(this.state == "not set" || this.state.isEmpty()){
            user.state =""
        }
        if(this.city == "not set" || this.city.isEmpty()){
            user.city =""
        }
        if(this.artistType == "not set" || this.artistType.isEmpty()){
            user.artistType =""
        }
        if(this.ethnicity == "not set" || this.ethnicity.isEmpty()){
            user.ethnicity =""
        }
        if(this.languages == "not set" || this.languages.isEmpty()){
            user.languages =""
        }
        if(this.experience == "not set" || this.experience.isEmpty()){
            user.experience =""
        }
        if(this.preferences == "not set" || this.preferences.isEmpty()){
            user.preferences =""
        }

        return user
    }

}