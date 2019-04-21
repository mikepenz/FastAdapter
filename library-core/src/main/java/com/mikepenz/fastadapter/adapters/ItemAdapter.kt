package com.mikepenz.fastadapter.adapters

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.IItemList
import com.mikepenz.fastadapter.utils.InterceptorUtil

/**
 * Kotlin type alias to simplify usage for an all accepting ItemAdapter
 */
typealias GenericItemAdapter = ItemAdapter<IItem<out RecyclerView.ViewHolder>>

/**
 * Created by mikepenz on 27.12.15.
 * A general ItemAdapter implementation based on the AbstractAdapter to speed up development for general items
 */
open class ItemAdapter<Item : IItem<out RecyclerView.ViewHolder>> : ModelAdapter<Item, Item> {

    constructor() : super(InterceptorUtil.DEFAULT as (element: Item) -> Item?)

    constructor(itemList: IItemList<Item>) : super(itemList, InterceptorUtil.DEFAULT as (element: Item) -> Item?)

    companion object {

        /**
         * static method to retrieve a new `ItemAdapter`
         *
         * @return a new ItemAdapter
         */
        @JvmStatic
        fun <Item : IItem<out RecyclerView.ViewHolder>> items(): ItemAdapter<Item> {
            return ItemAdapter()
        }
    }
}
