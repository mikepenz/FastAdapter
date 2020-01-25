package com.mikepenz.fastadapter.utils

import android.util.SparseArray
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.ITypeInstanceCache

/**
 * Created by fabianterhorst on 24.08.17.
 */
class DefaultTypeInstanceCache<Item : GenericItem> :
        ITypeInstanceCache<Item> {

    // we remember all possible types so we can create a new view efficiently
    private val typeInstances = SparseArray<Item>()

    override fun register(item: Item): Boolean {
        if (typeInstances.indexOfKey(item.type) < 0) {
            typeInstances.put(item.type, item)
            return true
        }
        return false
    }

    override fun get(type: Int): Item {
        return typeInstances.get(type)
    }

    override fun clear() {
        typeInstances.clear()
    }
}
