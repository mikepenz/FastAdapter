package com.mikepenz.fastadapter.listeners

import com.mikepenz.fastadapter.GenericItem

/**
 * interface for the ItemFilterListener
 */
interface ItemFilterListener<Item : GenericItem> {
    fun itemsFiltered(constraint: CharSequence?, results: List<Item>?)

    fun onReset()
}
