package com.mikepenz.fastadapter.extensions

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IItem

interface ExtensionFactory<T : IAdapterExtension<out IItem<out RecyclerView.ViewHolder>>> {

    val clazz: Class<T>

    fun create(fastAdapter: FastAdapter<out IItem<out RecyclerView.ViewHolder>>): T?
}
