package com.mikepenz.fastadapter.listeners

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.R

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
    fun getFastAdapter(viewHolder: RecyclerView.ViewHolder): FastAdapter<Item>? {
        val tag = viewHolder.itemView.getTag(R.id.fastadapter_item_adapter)
        return if (tag is FastAdapter<*>) {
            tag as FastAdapter<Item>
        } else null
    }

    /**
     * helper method to get the item for this ViewHolder
     *
     * @param viewHolder
     * @return
     */
    fun getItem(viewHolder: RecyclerView.ViewHolder): Item? {
        val adapter = getFastAdapter(viewHolder) ?: return null
        //we get the adapterPosition from the viewHolder
        val pos = adapter.getHolderAdapterPosition(viewHolder)
        //make sure the click was done on a valid item
        return if (pos != RecyclerView.NO_POSITION) {
            //we update our item with the changed property
            adapter.getItem(pos)
        } else null
    }
}
