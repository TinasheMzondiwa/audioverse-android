package com.tinashe.audioverse.ui.player

import androidx.recyclerview.widget.DiffUtil
import com.tinashe.audioverse.data.model.Recording

class MediaDiffCallback(private val oldList: List<Recording>,
                        private val newList: List<Recording>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        //TODO: Add more comparisons for new properties like thumbs up, added to list etc

        return oldItem.id == newItem.id
    }
}