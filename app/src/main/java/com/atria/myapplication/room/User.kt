package com.atria.myapplication.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey(autoGenerate = true)var uId:Int,
    var name :String,
    var email :String,
    var gender :String,
    var date :String,
    var phNumber:String,
    var username:String = "not set",
    var userType:String,
    var auditionType:String
)