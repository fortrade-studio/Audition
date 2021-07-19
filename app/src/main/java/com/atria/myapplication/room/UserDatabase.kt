package com.atria.myapplication.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class],version = 1,exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun getUserDao():UserDao

    companion object{
         @Volatile private var data : UserDatabase? = null

        fun getDatabase(context: Context):UserDatabase{
            var temp = data

            if(temp==null){
                temp = Room.databaseBuilder(context,UserDatabase::class.java,"UserSchema")
                    .fallbackToDestructiveMigration()
                    .build()
                data = temp
            }
            return data!!
        }

    }


}