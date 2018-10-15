package com.mikepenz.fastadapter.extensions

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IItem

interface ExtensionFactory<Item: IItem<out RecyclerView.ViewHolder>> {

    val clazz: Class<out IAdapterExtension<out IItem<out RecyclerView.ViewHolder>>>

    fun create(
        fastAdapter: FastAdapter<Item>,
        clazz: Class<IAdapterExtension<Item>>
    ): IAdapterExtension<Item>?
}
