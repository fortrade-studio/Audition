package com.atria.myapplication.room

class UserRepository(
   private val dao: UserDao
) {

    fun getUser():List<User>{
        return dao.getUser()
    }

    fun insertUser(user:User){
        dao.insertUser(user)
    }

    fun updateUser(userType:String, auditionType:String , phNumber:String){
        dao.updateUser(userType,auditionType,phNumber)
    }
}