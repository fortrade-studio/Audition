package com.atria.myapplication.diffutils

import androidx.recyclerview.widget.DiffUtil

class VideoDiffUtils(val newList: List<VideoData>,val oldList: List<VideoData>): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].link == oldList[oldItemPosition].link
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].link == oldList[oldItemPosition].link
    }

}
    data class VideoData(
        val link:String="",
        val likes : Long = 0L,
        val userid : String = "",
        val uvid : String = ""
)