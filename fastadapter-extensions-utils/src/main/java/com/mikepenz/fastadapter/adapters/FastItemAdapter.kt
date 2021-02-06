package com.mikepenz.fastadapter.adapters

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter.Companion.items

/**
 * Kotlin type alias to simplify usage for an all accepting FastItemAdapter
 */
typealias GenericFastItemAdapter = FastItemAdapter<GenericItem>

/**
 * Created by mikepenz on 18.01.16.
 */
open class FastItemAdapter<Item : GenericItem>(
        /** @return the internal created [ItemAdapter] */
        val itemAdapter: ItemAdapter<Item> = items()
) : IItemAdapter<Item, Item> by itemAdapter, FastAdapter<Item>() {

    /** @return the filter used to filter items */
    val itemFilter: ItemFilter<*, Item>
        get() = itemAdapter.itemFilter

    /** ctor */
    init {
        addAdapter<IAdapter<Item>>(0, itemAdapter)
        cacheSizes()
    }

    /**
     * Defines if the IdDistributor is used to provide an ID to all added items which do not yet define an id
     *
     * @param useIdDistributor false if the IdDistributor shouldn't be used
     * @return this
     */
    @Deprecated(message = "Use the isUseIdDistributor property getter", replaceWith = ReplaceWith("isUseIdDistributor"), level = DeprecationLevel.WARNING)
    open fun withUseIdDistributor(useIdDistributor: Boolean): FastItemAdapter<Item> {
        itemAdapter.isUseIdDistributor = useIdDistributor
        return this
    }

    /**
     * Removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount the count of items removed
     */
    @Deprecated(message = "removeItemRange is deprecated", replaceWith = ReplaceWith("removeRange"), level = DeprecationLevel.WARNING)
    open fun removeItemRange(position: Int, itemCount: Int): FastItemAdapter<Item> {
        removeRange(position, itemCount)
        return this
    }

    /** Convenient open functions, to force to remap all possible types for the RecyclerView */
    open fun remapMappedTypes() {
        itemAdapter.remapMappedTypes()
    }
}
