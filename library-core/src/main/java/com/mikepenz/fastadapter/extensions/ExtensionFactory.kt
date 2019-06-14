package com.mikepenz.fastadapter.extensions

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IItem

interface ExtensionFactory {

    val clazz: Class<out IAdapterExtension<out IItem<out RecyclerView.ViewHolder>>>

    fun create(
            fastAdapter: FastAdapter<out IItem<out RecyclerView.ViewHolder>>
    ): IAdapterExtension<out IItem<out RecyclerView.ViewHolder>>?
}
