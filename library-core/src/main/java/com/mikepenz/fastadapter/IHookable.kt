package com.mikepenz.fastadapter

import com.mikepenz.fastadapter.listeners.EventHook

/**
 * Created by fabianterhorst on 01.03.17.
 */
interface IHookable<Item : GenericItem> {
    /**
     * @return the event hooks for the item
     */
    val eventHooks: List<EventHook<Item>>?
}
