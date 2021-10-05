package com.atria.myapplication.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import java.lang.StringBuilder

class NumberToUniqueStringGenerator {

    companion object {

        val firebase = FirebaseAuth.getInstance().currentUser?.phoneNumber
        private val alphabet: CharArray =
            charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j')

        fun numberToUniqueString(number: String): String {
            val stringBuilder = StringBuilder()
            for (num in number.removePrefix("+")) {
                stringBuilder.append(alphabet[num.toString().toInt()])
            }
            Log.i("NumberToUnique", "numberToUniqueString: ${stringBuilder.toString()}")
            return stringBuilder.toString().trim()
        }

        //jbjggicdaiid
        fun userUniqueString(): String {
            val stringBuilder = StringBuilder()
            for (num in firebase!!.removePrefix("+")) {
                stringBuilder.append(alphabet[num.toString().toInt()])
            }
            return stringBuilder.toString().trim()
        }

    }

}