package com.mikepenz.fastadapter.listeners

import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem

abstract class LongClickEventHook<Item : GenericItem> : EventHook<Item> {
    abstract fun onLongClick(v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item): Boolean
}
