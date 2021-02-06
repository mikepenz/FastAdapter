package com.mikepenz.fastadapter.select

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.extensions.ExtensionFactory

class SelectExtensionFactory : ExtensionFactory<SelectExtension<*>> {

    override val clazz = SelectExtension::class.java

    override fun create(fastAdapter: FastAdapter<out GenericItem>): SelectExtension<*>? {
        return SelectExtension(fastAdapter)
    }
}
