package com.mikepenz.fastadapter.listeners

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem

abstract class ClickEventHook<Item : GenericItem> : EventHook<Item> {
    abstract fun onClick(v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item)
}

/**
 * Convenient extension function to simplify adding a [ClickEventHook] to the [FastAdapter]
 *
 * A sample implementation may look like:
 * ```
 * fastAdapter.addClickListener({ vh: SimpleImageItem.ViewHolder -> vh.imageView }) { _, _, _, _ ->
 *    // do something
 * }
 * ```
 */
inline fun <reified VH : RecyclerView.ViewHolder, reified Item : GenericItem> FastAdapter<Item>.addClickListener(crossinline resolveView: (VH) -> View?, crossinline resolveViews: ((VH) -> List<View>?) = { null }, crossinline onClick: (v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item) -> Unit) {
    addEventHook(object : ClickEventHook<Item>() {
        override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
            return if (viewHolder is VH) resolveView.invoke(viewHolder) else super.onBind(viewHolder)
        }

        override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
            return if (viewHolder is VH) resolveViews.invoke(viewHolder) else super.onBindMany(viewHolder)
        }

        override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item) {
            onClick.invoke(v, position, fastAdapter, item)
        }
    })
}