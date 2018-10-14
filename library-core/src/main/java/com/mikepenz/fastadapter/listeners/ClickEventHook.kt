package com.mikepenz.fastadapter.listeners

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem

abstract class ClickEventHook<Item : IItem<out RecyclerView.ViewHolder>> : EventHook<Item> {
    abstract fun onClick(v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item)

    override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
        return null
    }

    override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
        return null
    }
}
