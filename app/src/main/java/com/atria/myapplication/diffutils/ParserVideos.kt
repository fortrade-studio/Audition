package com.atria.myapplication.diffutils

import java.io.Serializable

data class ParserVideos(
    val list: List<String>,
    val id:String,
    val pos:Int
) : Serializable{
}