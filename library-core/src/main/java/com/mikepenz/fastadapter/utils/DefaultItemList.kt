package com.mikepenz.fastadapter.utils

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IItemList

/**
 * The default item list
 */

abstract class DefaultItemList<Item : GenericItem> : IItemList<Item> {

    var fastAdapter: FastAdapter<Item>? = null
}
