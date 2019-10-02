package com.mikepenz.fastadapter.utils

import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IFastAdapter
import com.mikepenz.fastadapter.IItemList

/**
 * The default item list
 */

abstract class DefaultItemList<Item : GenericItem> : IItemList<Item> {

    var fastAdapter: IFastAdapter<Item>? = null
}
