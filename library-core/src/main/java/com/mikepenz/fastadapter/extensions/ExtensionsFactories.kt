package com.mikepenz.fastadapter.extensions

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IFastAdapter

object ExtensionsFactories {

    private val factories = LinkedHashMap<Class<out IAdapterExtension<out GenericItem>>, ExtensionFactory<*>>()

    fun register(factory: ExtensionFactory<*>) {
        factories[factory.clazz] = factory
    }

    fun create(
            fastAdapter: IFastAdapter<out GenericItem>,
            clazz: Class<out IAdapterExtension<out GenericItem>>
    ): IAdapterExtension<out GenericItem>? =
            factories[clazz]?.create(fastAdapter)

    inline fun <reified T : IAdapterExtension<out GenericItem>>
            create(fastAdapter: FastAdapter<out GenericItem>): IAdapterExtension<out GenericItem>? =
            create(fastAdapter, T::class.java)
}
