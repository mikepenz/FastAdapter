package com.mikepenz.fastadapter.extensions

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IItem

object ExtensionsFactories {

    private val factories = LinkedHashMap<Class<out IAdapterExtension<out IItem<out RecyclerView.ViewHolder>>>, ExtensionFactory<*>>()

    fun register(factory: ExtensionFactory<*>) {
        factories[factory.clazz] = factory
    }

    fun create(
            fastAdapter: FastAdapter<out IItem<out RecyclerView.ViewHolder>>,
            clazz: Class<out IAdapterExtension<out IItem<out RecyclerView.ViewHolder>>>
    ): IAdapterExtension<out IItem<out RecyclerView.ViewHolder>>? =
            factories[clazz]?.create(fastAdapter)

    inline fun <reified T : IAdapterExtension<out IItem<out RecyclerView.ViewHolder>>>
            create(fastAdapter: FastAdapter<out IItem<out RecyclerView.ViewHolder>>): IAdapterExtension<out IItem<out RecyclerView.ViewHolder>>? =
            create(fastAdapter, T::class.java)
}
