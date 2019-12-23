package com.mikepenz.fastadapter.utils

import com.mikepenz.fastadapter.GenericItem
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

/**
 * The default item list implementation
 */
class ComparableItemListImpl<Item : GenericItem> @JvmOverloads constructor(
        comparator: Comparator<Item>?,
        items: MutableList<Item> = ArrayList()
) : DefaultItemListImpl<Item>(items) {

    /** @return the defined Comparator used for this ItemAdaper */
    var comparator: Comparator<Item>? = comparator
        private set

    /**
     * Define a comparator which will be used to sort the list "everytime" it is altered
     * NOTE this will only sort if you "set" a new list or "add" new items (not if you provide a position for the add function)
     *
     * @param comparator used to sort the list
     * @param sortNow    specifies if we use the provided comparator to sort now
     * @return this
     */
    @JvmOverloads
    fun withComparator(
            comparator: Comparator<Item>?,
            sortNow: Boolean = true
    ): ComparableItemListImpl<Item> {
        this.comparator = comparator

        //we directly sort the list with the defined comparator
        if (this.comparator != null && sortNow) {
            Collections.sort(_items, this.comparator)
            fastAdapter?.notifyAdapterDataSetChanged()
        }

        return this
    }

    override fun move(fromPosition: Int, toPosition: Int, preItemCount: Int) {
        val item = _items[fromPosition - preItemCount]
        _items.removeAt(fromPosition - preItemCount)
        _items.add(toPosition - preItemCount, item)
        _items.trySortWith(comparator)
        fastAdapter?.notifyAdapterDataSetChanged()
    }

    override fun addAll(items: List<Item>, preItemCount: Int) {
        _items.addAll(items)
        _items.trySortWith(comparator)
        fastAdapter?.notifyAdapterDataSetChanged()
    }

    override fun addAll(position: Int, items: List<Item>, preItemCount: Int) {
        _items.addAll(position - preItemCount, items)
        _items.trySortWith(comparator)
        fastAdapter?.notifyAdapterDataSetChanged()
    }

    override fun setNewList(items: List<Item>, notify: Boolean) {
        _items = ArrayList(items)
        _items.trySortWith(comparator)
        if (notify) {
            fastAdapter?.notifyAdapterDataSetChanged()
        }
    }
}