package com.mikepenz.fastadapter.adapters

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem

/**
 * Created by mikepenz on 03.03.16.
 */
class WrapAdapter<Item : IItem<VH>, VH : RecyclerView.ViewHolder>(items: List<Item>) : AbstractWrapAdapter<Item, VH>(items) {

    override var items: List<Item> = emptyList()
        set(items) {
            field = items
            notifyDataSetChanged()
        }

    override fun shouldInsertItemAtPosition(position: Int): Boolean {
        if (adapter?.itemCount ?: 0 > 0 && items.isNotEmpty()) {
            val itemsInBetween = (adapter?.itemCount ?: 0 + items.size) / (items.size + 1) + 1
            return (position + 1) % itemsInBetween == 0
        }
        return false
    }

    override fun itemInsertedBeforeCount(position: Int): Int {
        if (adapter?.itemCount ?: 0 > 0 && items.isNotEmpty()) {
            val itemsInBetween = (adapter?.itemCount ?: 0 + items.size) / (items.size + 1) + 1
            return position / itemsInBetween
        }
        return 0
    }
}
