package com.mikepenz.fastadapter.expandable

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.extensions.ExtensionFactory

class ExpandableExtensionFactory : ExtensionFactory<ExpandableExtension<*>> {

    override val clazz = ExpandableExtension::class.java

    override fun create(fastAdapter: FastAdapter<out GenericItem>): ExpandableExtension<*>? {
        return ExpandableExtension(fastAdapter)
    }
}
