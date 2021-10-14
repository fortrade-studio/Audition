package com.atria.myapplication

import androidx.lifecycle.MutableLiveData
import com.atria.myapplication.room.User

object Constants {
    val user = "Users"
    val reports="Reports"
    val notset = "not set"
    val username = "username"
    val title = "Username Not Picked"
    val message = "User Profile is incomplete without a username !! Please Pick a Suitable username"
    val top = "Top"
    val viewdata = "ViewData"
    val links = "links"
    val big = "big"
    val circular = "circular"
    val follow = "follower"
    val following = "following"
    val name = "name"
    val summary = "summary"
    val images = "images"
    val videos = "videos"
    var profile_id = ""
    val extras = "data"
    val current = "current"
    val user_id = "userId"

    //testing boolean
    var checkIfUser = 0 // 1 means yes it is userprofile , -1 means no

    var mainHomeFragment: MainHomeFragment ? = null
    var searchStringLiveData : MutableLiveData<String> = MutableLiveData()

    var isHome = false
    // https://ww.atria.com?jjafdjlsdf,auth.url="",
    var linkPrefix = "https://www.atria.com?"
    var backCallback : MutableLiveData<Int> = MutableLiveData(0)
    var homeToProfileCallback  = MutableLiveData("")
    var profile : User? = null
    var big_link :String = "https://firebasestorage.googleapis.com/v0/b/audition-15207.appspot.com/o/default%2Fbackground.jpg?alt=media&token=f80b49c5-bc9b-4e88-b7b2-e60fc372e8f6"
    var circular_link:String = "https://firebasestorage.googleapis.com/v0/b/audition-15207.appspot.com/o/default%2Fprofile.jpg?alt=media&token=9c73401c-6ac1-4826-932e-21c07d43b1a7"
}