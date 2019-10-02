package com.mikepenz.fastadapter.listeners

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapterEngine
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IFastAdapter

abstract class CustomEventHook<Item : GenericItem> : EventHook<Item> {
    /**
     * this method is called by the `FastAdapter` during ViewHolder creation ONCE.
     *
     * @param view
     * @param viewHolder
     */
    abstract fun attachEvent(view: View, viewHolder: RecyclerView.ViewHolder)

    /**
     * Helper method to get the FastAdapter from this ViewHolder
     *
     * @param viewHolder
     * @return
     */
    @Deprecated("Replaced with the new helper inside the FastAdapter class", ReplaceWith("FastAdapter.getFromHolderTag(viewHolder)", "com.mikepenz.fastadapter.FastAdapter"))
    fun getFastAdapter(viewHolder: RecyclerView.ViewHolder): IFastAdapter<Item>? = FastAdapterEngine.getFromHolderTag(viewHolder)

    /**
     * helper method to get the item for this ViewHolder
     *
     * @param viewHolder
     * @return
     */
    @Deprecated("Replaced with the new helper inside the FastAdapter class", ReplaceWith("FastAdapter.getHolderAdapterItem(viewHolder)", "com.mikepenz.fastadapter.FastAdapter"))
    fun getItem(viewHolder: RecyclerView.ViewHolder): Item? = FastAdapterEngine.getHolderAdapterItem(viewHolder)
}
