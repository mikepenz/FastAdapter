package com.mikepenz.fastadapter.utils

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IItemList

/**
 * The default item list
 */

abstract class DefaultItemList<Item : GenericItem> : IItemList<Item> {

    private var _fastAdapter: FastAdapter<Item>? = null

    var fastAdapter: FastAdapter<Item>?
        get() = if (active) _fastAdapter else null
        set(value) {
            _fastAdapter = value
        }

    override var active: Boolean = true
}
