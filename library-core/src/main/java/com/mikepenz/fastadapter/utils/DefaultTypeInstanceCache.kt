package com.mikepenz.fastadapter.utils

import android.util.SparseArray
import androidx.recyclerview.widget.RecyclerView

import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.ITypeInstanceCache

/**
 * Created by fabianterhorst on 24.08.17.
 */

class DefaultTypeInstanceCache<Item : IItem<out RecyclerView.ViewHolder>> :
        ITypeInstanceCache<Item> {

    // we remember all possible types so we can create a new view efficiently
    private val mTypeInstances = SparseArray<Item>()

    override fun register(item: Item): Boolean {
        if (mTypeInstances.indexOfKey(item.type) < 0) {
            mTypeInstances.put(item.type, item)
            return true
        }
        return false
    }

    override fun get(type: Int): Item {
        return mTypeInstances.get(type)
    }

    override fun clear() {
        mTypeInstances.clear()
    }
}
