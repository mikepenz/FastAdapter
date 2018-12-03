package com.mikepenz.fastadapter

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.listeners.OnClickListener

/**
 * Created by mikepenz on 25.02.16.
 */
interface IClickable<Item : IItem<out RecyclerView.ViewHolder>> {

    /**
     * Provide a listener which is called before any processing is done within the adapter
     * return true if you want to consume the event
     */
    var onPreItemClickListener: OnClickListener<Item>?

    /**
     * Provide a listener which is called before the click listener is called within the adapter
     * return true if you want to consume the event
     */
    var onItemClickListener: OnClickListener<Item>?
}
