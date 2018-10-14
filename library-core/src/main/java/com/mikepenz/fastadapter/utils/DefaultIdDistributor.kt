package com.mikepenz.fastadapter.utils

import com.mikepenz.fastadapter.IIdDistributor
import com.mikepenz.fastadapter.IIdentifyable

/**
 * Created by mikepenz on 19.09.15.
 */
abstract class DefaultIdDistributor<Identifiable : IIdentifyable> : IIdDistributor<Identifiable> {

    /**
     * set an unique identifier for all items which do not have one set already
     *
     * @param items
     * @return
     */
    override fun checkIds(items: List<Identifiable>): List<Identifiable> {
        var i = 0
        val size = items.size
        while (i < size) {
            checkId(items[i])
            i++
        }
        return items
    }

    /**
     * set an unique identifier for all items which do not have one set already
     *
     * @param items
     * @return
     */
    override fun checkIds(vararg items: Identifiable): Array<out Identifiable> {
        for (item in items) {
            checkId(item)
        }
        return items
    }

    /**
     * set an unique identifier for the item which do not have one set already
     *
     * @param item
     * @return
     */
    override fun checkId(item: Identifiable): Identifiable {
        if (item.identifier == -1L) {
            item.identifier = nextId(item)
        }
        return item
    }
}
