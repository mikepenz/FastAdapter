package com.mikepenz.fastadapter

import android.view.View

/**
 * Created by mikepenz on 25.02.16.
 */
typealias ClickListener<Item> = (v: View?, adapter: IAdapter<Item>, item: Item, position: Int) -> Boolean

typealias LongClickListener<Item> = (v: View, adapter: IAdapter<Item>, item: Item, position: Int) -> Boolean

interface IClickable<Item : GenericItem> {

    /**
     * Provide a listener which is called before any processing is done within the adapter
     * return true if you want to consume the event
     */
    var onPreItemClickListener: ClickListener<Item>?

    /**
     * Provide a listener which is called before the click listener is called within the adapter
     * return true if you want to consume the event
     */
    var onItemClickListener: ClickListener<Item>?
}
