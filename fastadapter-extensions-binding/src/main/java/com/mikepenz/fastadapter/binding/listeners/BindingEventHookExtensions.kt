package com.mikepenz.fastadapter.binding.listeners

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.listeners.LongClickEventHook
import com.mikepenz.fastadapter.listeners.TouchEventHook

/**
 * Convenient extension function to simplify adding a [ClickEventHook] to the [FastAdapter]
 *
 * A sample implementation may look like:
 * ```
 * fastAdapter.addClickListener<Binding, GenericItem>({ binding -> binding.view }) { v, position, fastAdapter, item ->
 *    // do something
 * }
 * ```
 */
inline fun <reified Binding : ViewBinding, reified Item : GenericItem> FastAdapter<Item>.addClickListener(crossinline resolveView: (Binding) -> View?, crossinline resolveViews: ((Binding) -> List<View>?) = { null }, crossinline onClick: (v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item) -> Unit) {
    addEventHook(object : ClickEventHook<Item>() {
        override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
            return if (viewHolder is BindingViewHolder<*> && viewHolder.binding is Binding) resolveView.invoke(viewHolder.binding) else super.onBind(viewHolder)
        }

        override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
            return if (viewHolder is BindingViewHolder<*> && viewHolder.binding is Binding) resolveViews.invoke(viewHolder.binding) else super.onBindMany(viewHolder)
        }

        override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item) {
            onClick.invoke(v, position, fastAdapter, item)
        }
    })
}


/**
 * Convenient extension function to simplify adding a [LongClickEventHook] to the [FastAdapter]
 *
 * A sample implementation may look like:
 * ```
 * fastAdapter.addLongClickListener<Binding, GenericItem>({ binding -> binding.view }) { v, position, fastAdapter, item ->
 *    // do something
 *    true
 * }
 * ```
 */
inline fun <reified Binding : ViewBinding, reified Item : GenericItem> FastAdapter<Item>.addLongClickListener(crossinline resolveView: (Binding) -> View?, crossinline resolveViews: ((Binding) -> List<View>?) = { null }, crossinline onLongClick: (v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item) -> Boolean) {
    addEventHook(object : LongClickEventHook<Item>() {
        override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
            return if (viewHolder is BindingViewHolder<*> && viewHolder.binding is Binding) resolveView.invoke(viewHolder.binding) else super.onBind(viewHolder)
        }

        override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
            return if (viewHolder is BindingViewHolder<*> && viewHolder.binding is Binding) resolveViews.invoke(viewHolder.binding) else super.onBindMany(viewHolder)
        }

        override fun onLongClick(v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item): Boolean {
            return onLongClick.invoke(v, position, fastAdapter, item)
        }
    })
}

/**
 * Convenient extension function to simplify adding a [TouchEventHook] to the [FastAdapter]
 *
 * A sample implementation may look like:
 * ```
 * fastAdapter.addTouchListener<Binding, GenericItem>({ binding -> binding.view }) { v, position, fastAdapter, item ->
 *    // do something
 *    true
 * }
 * ```
 */
inline fun <reified Binding : ViewBinding, reified Item : GenericItem> FastAdapter<Item>.addTouchListener(crossinline resolveView: (Binding) -> View?, crossinline resolveViews: ((Binding) -> List<View>?) = { null }, crossinline onTouch: (v: View, event: MotionEvent, position: Int, fastAdapter: FastAdapter<Item>, item: Item) -> Boolean) {
    addEventHook(object : TouchEventHook<Item>() {
        override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
            return if (viewHolder is BindingViewHolder<*> && viewHolder.binding is Binding) resolveView.invoke(viewHolder.binding) else super.onBind(viewHolder)
        }

        override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
            return if (viewHolder is BindingViewHolder<*> && viewHolder.binding is Binding) resolveViews.invoke(viewHolder.binding) else super.onBindMany(viewHolder)
        }

        override fun onTouch(v: View, event: MotionEvent, position: Int, fastAdapter: FastAdapter<Item>, item: Item): Boolean {
            return onTouch.invoke(v, event, position, fastAdapter, item)
        }
    })
}