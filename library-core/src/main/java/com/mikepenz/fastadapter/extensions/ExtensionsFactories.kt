package com.mikepenz.fastadapter.extensions

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IItem

object ExtensionsFactories {

    private val factories =
        LinkedHashMap<Class<out IAdapterExtension<out IItem<out RecyclerView.ViewHolder>>>, ExtensionFactory<*>>()

    fun register(factory: ExtensionFactory<*>) {
        factories[factory.clazz] = factory
    }
}
