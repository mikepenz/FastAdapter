package com.mikepenz.fastadapter.listeners

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem

abstract class TouchEventHook<Item : GenericItem> : EventHook<Item> {
    abstract fun onTouch(v: View, event: MotionEvent, position: Int, fastAdapter: FastAdapter<Item>, item: Item): Boolean
}

/**
 * Convenient extension function to simplify adding a [TouchEventHook] to the [FastAdapter]
 */
inline fun <reified VH : RecyclerView.ViewHolder, reified Item : GenericItem> FastAdapter<GenericItem>.addLongClickListener(crossinline resolveView: (VH) -> View?, crossinline resolveViews: ((VH) -> List<View>?) = { null }, crossinline onTouch: (v: View, event: MotionEvent, position: Int, fastAdapter: FastAdapter<Item>, item: Item) -> Boolean) {
    addEventHook(object : TouchEventHook<Item>() {
        override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
            return if (viewHolder is VH) resolveView.invoke(viewHolder) else null
        }

        override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
            return if (viewHolder is VH) resolveViews.invoke(viewHolder) else super.onBindMany(viewHolder)
        }

        override fun onTouch(v: View, event: MotionEvent, position: Int, fastAdapter: FastAdapter<Item>, item: Item): Boolean {
            return onTouch.invoke(v, event, position, fastAdapter, item)
        }
    })
}