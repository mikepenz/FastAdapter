package com.mikepenz.fastadapter.adapters

import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IItemList
import com.mikepenz.fastadapter.dsl.FastAdapterDsl
import com.mikepenz.fastadapter.utils.InterceptorUtil

/**
 * Kotlin type alias to simplify usage for an all accepting ItemAdapter
 */
typealias GenericItemAdapter = ItemAdapter<GenericItem>

/**
 * Created by mikepenz on 27.12.15.
 * A general ItemAdapter implementation based on the AbstractAdapter to speed up development for general items
 */
@FastAdapterDsl
open class ItemAdapter<Item : GenericItem> : ModelAdapter<Item, Item> {

    constructor() : super(InterceptorUtil.DEFAULT as (element: Item) -> Item?)

    constructor(itemList: IItemList<Item>) : super(itemList, InterceptorUtil.DEFAULT as (element: Item) -> Item?)

    companion object {

        /**
         * Static method to retrieve a new `ItemAdapter`
         *
         * @return a new ItemAdapter
         */
        @JvmStatic
        fun <Item : GenericItem> items(): ItemAdapter<Item> {
            return ItemAdapter()
        }
    }
}
