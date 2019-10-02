package com.mikepenz.fastadapter.listeners

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IFastAdapter

abstract class LongClickEventHook<Item : GenericItem> : EventHook<Item> {
    abstract fun onLongClick(v: View, position: Int, fastAdapter: IFastAdapter<Item>, item: Item): Boolean
}

/**
 * Convenient extension function to simplify adding a [LongClickEventHook] to the [FastAdapter]
 *
 * A sample implementation may look like:
 * mFastAdapter.addLongClickListener({ vh: SimpleImageItem.ViewHolder -> vh.imageView }) { _, _, _, _ ->
 *    // do something
 *    true
 * }
 */
inline fun <reified VH : RecyclerView.ViewHolder, reified Item : GenericItem> IFastAdapter<Item>.addLongClickListener(crossinline resolveView: (VH) -> View?, crossinline resolveViews: ((VH) -> List<View>?) = { null }, crossinline onLongClick: (v: View, position: Int, fastAdapter: IFastAdapter<Item>, item: Item) -> Boolean) {
    addEventHook(object : LongClickEventHook<Item>() {
        override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
            return if (viewHolder is VH) resolveView.invoke(viewHolder) else null
        }

        override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
            return if (viewHolder is VH) resolveViews.invoke(viewHolder) else super.onBindMany(viewHolder)
        }

        override fun onLongClick(v: View, position: Int, fastAdapter: IFastAdapter<Item>, item: Item): Boolean {
            return onLongClick.invoke(v, position, fastAdapter, item)
        }
    })
}