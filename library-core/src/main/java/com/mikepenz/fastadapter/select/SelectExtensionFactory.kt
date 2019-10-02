package com.mikepenz.fastadapter.select

import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IFastAdapter
import com.mikepenz.fastadapter.extensions.ExtensionFactory

class SelectExtensionFactory : ExtensionFactory<SelectExtension<*>> {

    override val clazz = SelectExtension::class.java

    override fun create(fastAdapter: IFastAdapter<out GenericItem>): SelectExtension<*>? {
        return SelectExtension(fastAdapter)
    }
}
