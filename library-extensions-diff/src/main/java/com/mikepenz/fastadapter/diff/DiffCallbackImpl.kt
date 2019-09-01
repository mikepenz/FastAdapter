package com.mikepenz.fastadapter.diff

import com.mikepenz.fastadapter.IItem

/**
 * Created by mikepenz on 24.08.16.
 */
internal class DiffCallbackImpl<Item : IItem<*>> : DiffCallback<Item> {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.identifier == newItem.identifier
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Item, oldItemPosition: Int, newItem: Item, newItemPosition: Int): Any? {
        return null
    }
}
