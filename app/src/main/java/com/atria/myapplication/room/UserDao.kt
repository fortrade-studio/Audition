package com.atria.myapplication.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Query("Select * from user")
    fun getUser():List<User>

    @Insert
    fun insertUser(user:User)

    @Query("Update user SET userType=:userType , auditionType=:auditionType where phNumber=:phNumber")
    fun updateUser(userType:String , auditionType:String,phNumber: String)

}