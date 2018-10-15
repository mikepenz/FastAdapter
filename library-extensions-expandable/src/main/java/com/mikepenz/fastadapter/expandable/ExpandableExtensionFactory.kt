package com.mikepenz.fastadapter.expandable

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.extensions.ExtensionFactory

class ExpandableExtensionFactory: ExtensionFactory<IItem<out RecyclerView.ViewHolder>> {

    override val clazz = ExpandableExtension::class.java

    override fun create(
        fastAdapter: FastAdapter<IItem<out RecyclerView.ViewHolder>>,
        clazz: Class<IAdapterExtension<IItem<out RecyclerView.ViewHolder>>>
    ): IAdapterExtension<IItem<out RecyclerView.ViewHolder>>? {
        (fastAdapter as? FastAdapter<ISubItem<*, *>>?)?.let { subItemAdapter ->
            return ExpandableExtension(subItemAdapter) as IAdapterExtension<IItem<out RecyclerView.ViewHolder>>
        }
        return null
    }
}
