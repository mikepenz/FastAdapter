package com.mikepenz.fastadapter.extensions

import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IFastAdapter

interface ExtensionFactory<T : IAdapterExtension<out GenericItem>> {

    val clazz: Class<T>

    fun create(fastAdapter: IFastAdapter<out GenericItem>): T?
}
