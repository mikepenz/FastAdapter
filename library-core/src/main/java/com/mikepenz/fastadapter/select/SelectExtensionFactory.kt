package com.mikepenz.fastadapter.select

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.extensions.ExtensionFactory

class SelectExtensionFactory: ExtensionFactory<IItem<out RecyclerView.ViewHolder>> {

    override val clazz = SelectExtension::class.java

    override fun create(
        fastAdapter: FastAdapter<IItem<out RecyclerView.ViewHolder>>,
        clazz: Class<IAdapterExtension<IItem<out RecyclerView.ViewHolder>>>
    ): IAdapterExtension<IItem<out RecyclerView.ViewHolder>>? {
        return SelectExtension(fastAdapter)
    }
}
