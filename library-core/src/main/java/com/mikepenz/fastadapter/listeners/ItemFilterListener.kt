package com.mikepenz.fastadapter.listeners

import android.support.v7.widget.RecyclerView
import com.mikepenz.fastadapter.IItem

/**
 * interface for the ItemFilterListener
 */
interface ItemFilterListener<Item : IItem<out RecyclerView.ViewHolder>> {
    fun itemsFiltered(constraint: CharSequence?, results: List<Item>?)

    fun onReset()
}
