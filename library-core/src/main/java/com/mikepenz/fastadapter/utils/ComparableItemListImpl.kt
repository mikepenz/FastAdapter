package com.mikepenz.fastadapter.utils

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import java.util.*

/**
 * The default item list implementation
 */

class ComparableItemListImpl<Item : IItem<out RecyclerView.ViewHolder>> :
        DefaultItemListImpl<Item> {

    /**
     * @return the defined Comparator used for this ItemAdaper
     */
    var comparator: Comparator<Item>? = null
        private set

    constructor(comparator: Comparator<Item>?) {
        this.mItems = ArrayList()
        this.comparator = comparator
    }

    constructor(comparator: Comparator<Item>?, items: MutableList<Item>) {
        this.mItems = items
        this.comparator = comparator
    }

    /**
     * define a comparator which will be used to sort the list "everytime" it is altered
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
            Collections.sort(mItems, this.comparator)
            fastAdapter!!.notifyAdapterDataSetChanged()
        }

        return this
    }

    override fun move(fromPosition: Int, toPosition: Int, preItemCount: Int) {
        val item = mItems[fromPosition - preItemCount]
        mItems.removeAt(fromPosition - preItemCount)
        mItems.add(toPosition - preItemCount, item)
        if (comparator != null) {
            Collections.sort(mItems, comparator)
        }
        fastAdapter!!.notifyAdapterDataSetChanged()
    }

    override fun addAll(items: List<Item>, preItemCount: Int) {
        mItems.addAll(items)
        if (comparator != null) {
            Collections.sort(mItems, comparator)
        }
        fastAdapter!!.notifyAdapterDataSetChanged()
    }

    override fun addAll(position: Int, items: List<Item>, preItemCount: Int) {
        mItems.addAll(position - preItemCount, items)
        if (comparator != null) {
            Collections.sort(mItems, comparator)
        }
        fastAdapter!!.notifyAdapterDataSetChanged()
    }

    override fun setNewList(items: List<Item>, notify: Boolean) {
        mItems = ArrayList(items)
        if (comparator != null) {
            Collections.sort(mItems, comparator)
        }
        if (notify) {
            fastAdapter!!.notifyAdapterDataSetChanged()
        }
    }
}
/**
 * define a comparator which will be used to sort the list "everytime" it is altered
 * NOTE this will only sort if you "set" a new list or "add" new items (not if you provide a position for the add function)
 *
 * @param comparator used to sort the list
 * @return this
 */
