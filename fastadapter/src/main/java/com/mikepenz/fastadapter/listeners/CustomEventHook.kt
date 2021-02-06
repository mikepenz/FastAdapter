package com.mikepenz.fastadapter.listeners

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem

abstract class CustomEventHook<Item : GenericItem> : EventHook<Item> {
    /**
     * This method is called by the `FastAdapter` during ViewHolder creation ONCE.
     */
    abstract fun attachEvent(view: View, viewHolder: RecyclerView.ViewHolder)

    /**
     * Helper method to get the FastAdapter from this ViewHolder
     */
    @Deprecated("Replaced with the new helper inside the FastAdapter class", ReplaceWith("FastAdapter.getFromHolderTag(viewHolder)", "com.mikepenz.fastadapter.FastAdapter"))
    fun getFastAdapter(viewHolder: RecyclerView.ViewHolder): FastAdapter<Item>? =
            FastAdapter.getFromHolderTag(viewHolder)

    /**
     * Helper method to get the item for this ViewHolder
     */
    @Deprecated("Replaced with the new helper inside the FastAdapter class", ReplaceWith("FastAdapter.getHolderAdapterItem(viewHolder)", "com.mikepenz.fastadapter.FastAdapter"))
    fun getItem(viewHolder: RecyclerView.ViewHolder): Item? =
            FastAdapter.getHolderAdapterItem(viewHolder)
}
