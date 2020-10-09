package com.mikepenz.fastadapter.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.R
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.listeners.CustomEventHook
import com.mikepenz.fastadapter.listeners.EventHook
import com.mikepenz.fastadapter.listeners.LongClickEventHook
import com.mikepenz.fastadapter.listeners.TouchEventHook

/**
 * Binds the hooks to the viewHolder
 *
 * @param viewHolder the viewHolder of the item
 */
internal fun List<EventHook<out GenericItem>>.bind(viewHolder: RecyclerView.ViewHolder) {
    for (event in this) {
        val view = event.onBind(viewHolder)
        if (view != null) {
            event.attachToView(viewHolder, view)
        }
        val views = event.onBindMany(viewHolder)
        if (views != null) {
            for (v in views) {
                event.attachToView(viewHolder, v)
            }
        }
    }
}

/**
 * Attaches the specific event to a view
 *
 * @param viewHolder the viewHolder containing this view
 * @param view       the view to attach to
 */
internal fun <Item : GenericItem> EventHook<Item>.attachToView(viewHolder: RecyclerView.ViewHolder, view: View) {
    when (this) {
        is ClickEventHook<*> -> view.setOnClickListener { v ->
            //get the adapter for this view
            val tagAdapter = viewHolder.itemView.getTag(R.id.fastadapter_item_adapter)
            val adapter = tagAdapter as? FastAdapter<Item> ?: return@setOnClickListener
            //we get the adapterPosition from the viewHolder
            val pos = adapter.getHolderAdapterPosition(viewHolder)
            //make sure the click was done on a valid item
            if (pos != RecyclerView.NO_POSITION) {
                val item: Item? = FastAdapter.getHolderAdapterItemTag(viewHolder)
                if (item != null) {
                    //we update our item with the changed property
                    (this as ClickEventHook<Item>).onClick(v, pos, adapter, item)
                }
            }
        }
        is LongClickEventHook<*> -> view.setOnLongClickListener(View.OnLongClickListener { v ->
            //get the adapter for this view
            val tagAdapter = viewHolder.itemView.getTag(R.id.fastadapter_item_adapter)
            val adapter = tagAdapter as? FastAdapter<Item> ?: return@OnLongClickListener false
            //we get the adapterPosition from the viewHolder
            val pos = adapter.getHolderAdapterPosition(viewHolder)
            //make sure the click was done on a valid item
            if (pos != RecyclerView.NO_POSITION) {
                val item: Item? = FastAdapter.getHolderAdapterItemTag(viewHolder)
                if (item != null) {
                    //we update our item with the changed property
                    return@OnLongClickListener (this as LongClickEventHook<Item>).onLongClick(v, pos, adapter, item)
                }
            }
            false
        })
        is TouchEventHook<*> -> view.setOnTouchListener(View.OnTouchListener { v, e ->
            //get the adapter for this view
            val tagAdapter = viewHolder.itemView.getTag(R.id.fastadapter_item_adapter)
            val adapter = tagAdapter as? FastAdapter<Item> ?: return@OnTouchListener false
            //we get the adapterPosition from the viewHolder
            val pos = adapter.getHolderAdapterPosition(viewHolder)
            //make sure the click was done on a valid item
            if (pos != RecyclerView.NO_POSITION) {
                val item: Item? = FastAdapter.getHolderAdapterItemTag(viewHolder)
                if (item != null) {
                    //we update our item with the changed property
                    return@OnTouchListener (this as TouchEventHook<Item>).onTouch(v, e, pos, adapter, item)
                }
            }
            false
        })
        //we trigger the event binding
        is CustomEventHook<*> -> (this as CustomEventHook<Item>).attachEvent(view, viewHolder)
    }
}
