package com.mikepenz.fastadapter.expandable

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.extensions.ExtensionFactory

class ExpandableExtensionFactory: ExtensionFactory {

    override val clazz = ExpandableExtension::class.java

    override fun create(
        fastAdapter: FastAdapter<out IItem<out RecyclerView.ViewHolder>>
    ): IAdapterExtension<out IItem<out RecyclerView.ViewHolder>>? {
        return ExpandableExtension(fastAdapter)
    }
}
