package com.mikepenz.fastadapter.listeners

import android.view.MotionEvent
import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem

abstract class TouchEventHook<Item : GenericItem> : EventHook<Item> {
    abstract fun onTouch(v: View, event: MotionEvent, position: Int, fastAdapter: FastAdapter<Item>, item: Item): Boolean
}
