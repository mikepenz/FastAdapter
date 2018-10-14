package com.mikepenz.fastadapter.utils

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.IItemList

import androidx.recyclerview.widget.RecyclerView

/**
 * The default item list
 */

abstract class DefaultItemList<Item : IItem<out RecyclerView.ViewHolder>> : IItemList<Item> {

    var fastAdapter: FastAdapter<Item>? = null
}
