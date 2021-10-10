package com.atria.myapplication

import androidx.lifecycle.MutableLiveData
import com.atria.myapplication.room.User

object Constants {
    val user = "Users"
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

    // https://ww.atria.com?jjafdjlsdf,auth.url="",
    var linkPrefix = "https://www.atria.com?"
    var backCallback : MutableLiveData<Int> = MutableLiveData(0)
    var homeToProfileCallback  = MutableLiveData("")
    var profile : User? = null
    var big_link :String = "https://www.knivesindia.com/ecom/wp-content/uploads/2017/06/wood-blog-placeholder.jpg"
    var circular_link:String = "https://holmesbuilders.com/wp-content/uploads/2016/12/male-profile-image-placeholder.png"
}