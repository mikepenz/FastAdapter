package com.mikepenz.fastadapter.extensions

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapterExtension

interface ExtensionFactory<T : IAdapterExtension<out GenericItem>> {

    val clazz: Class<T>

    fun create(fastAdapter: FastAdapter<out GenericItem>): T?
}
