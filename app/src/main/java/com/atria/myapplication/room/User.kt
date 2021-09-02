package com.atria.myapplication.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey(autoGenerate = true)var uId:Int=0,
    var name :String= "not set",
    var email :String= "not set",
    var gender :String= "not set",
    var date :String= "not set",
    var phNumber:String= "not set",
    var username:String = "not set",
    var userType:String= "not set",
    var auditionType:String= "not set",
    var city:String= "not set",
    var country:String= "not set",
    var state:String= "not set",
    var artistType:String= "not set",
    var ethnicity:String= "not set",
    var languages:String= "not set",
    var preferences:String= "not set",
    var experience:String= "not set",
    var summary:String  = "not set"
)