package com.mikepenz.fastadapter.select

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.extensions.ExtensionFactory

class SelectExtensionFactory : ExtensionFactory<SelectExtension<*>> {

    override val clazz = SelectExtension::class.java

    override fun create(
        fastAdapter: FastAdapter<out IItem<out RecyclerView.ViewHolder>>
    ): SelectExtension<*>? {
        return SelectExtension(fastAdapter)
    }
}
