package com.mikepenz.fastadapter

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.listeners.EventHook

/**
 * Created by fabianterhorst on 01.03.17.
 */
interface IHookable<Item : IItem<out RecyclerView.ViewHolder>> {
    /**
     * @return the event hooks for the item
     */
    val eventHooks: List<EventHook<Item>>?
}
