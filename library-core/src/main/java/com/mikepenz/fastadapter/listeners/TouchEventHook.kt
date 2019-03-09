package com.mikepenz.fastadapter.listeners

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem

abstract class TouchEventHook<Item : IItem<out RecyclerView.ViewHolder>> : EventHook<Item> {
    abstract fun onTouch(v: View, event: MotionEvent, position: Int, fastAdapter: FastAdapter<Item>, item: Item): Boolean
}
