package com.mikepenz.fastadapter.expandable

import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IFastAdapter
import com.mikepenz.fastadapter.extensions.ExtensionFactory

class ExpandableExtensionFactory : ExtensionFactory<ExpandableExtension<*>> {

    override val clazz = ExpandableExtension::class.java

    override fun create(fastAdapter: IFastAdapter<out GenericItem>): ExpandableExtension<*>? {
        return ExpandableExtension(fastAdapter)
    }
}
