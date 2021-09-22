package com.atria.myapplication.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.atria.myapplication.viewModel.search.SearchFragmentViewModel

class SearchDiffUtils(
    val new : ArrayList<SearchFragmentViewModel.SearchUser>,
    val old : ArrayList<SearchFragmentViewModel.SearchUser>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return new[newItemPosition].username == old[oldItemPosition].username
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return new[newItemPosition].username == old[oldItemPosition].username
    }

}