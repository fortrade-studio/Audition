package com.atria.myapplication.utils

import com.google.firebase.auth.FirebaseAuth
import java.lang.StringBuilder

class NumberToUniqueStringGenerator {

    companion object{

        val firebase = FirebaseAuth.getInstance().currentUser?.phoneNumber
        private val alphabet:CharArray = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j')

        fun numberToUniqueString(number:String):String{
            val stringBuilder  = StringBuilder()
            number.removePrefix("+").forEach {
                val numb = it.toInt()
                stringBuilder.append(alphabet[numb])
            }

            return stringBuilder.toString()
        }

        fun userUniqueString():String{
            val stringBuilder = StringBuilder()
            firebase!!.forEach {
                stringBuilder.append(alphabet.indexOf(it))
            }
            return stringBuilder.toString()
        }

    }

}
