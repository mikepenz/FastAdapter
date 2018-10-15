package com.mikepenz.fastadapter.adapters

import com.mikepenz.fastadapter.IInterceptor
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.IItemList

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by mikepenz on 27.12.15.
 * A general ItemAdapter implementation based on the AbstractAdapter to speed up development for general items
 */
class ItemAdapter<Item : IItem<out RecyclerView.ViewHolder>> : ModelAdapter<Item, Item> {

    constructor() : super(IInterceptor.DEFAULT as IInterceptor<Item, Item>) {}

    constructor(itemList: IItemList<Item>) : super(
        itemList,
        IInterceptor.DEFAULT as IInterceptor<Item, Item>
    )

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
