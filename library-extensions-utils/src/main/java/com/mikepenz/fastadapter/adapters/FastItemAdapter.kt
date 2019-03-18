package com.mikepenz.fastadapter.adapters

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter.Companion.items

/**
 * Kotlin type alias to simplify usage for an all accepting FastItemAdapter
 */
typealias GenericFastItemAdapter = FastItemAdapter<IItem<out RecyclerView.ViewHolder>>

/**
 * Created by mikepenz on 18.01.16.
 */
open class FastItemAdapter<Item : IItem<out RecyclerView.ViewHolder>> : FastAdapter<Item>() {
    /**
     * returns the internal created ItemAdapter
     *
     * @return the ItemAdapter used inside this FastItemAdapter
     */
    val itemAdapter: ItemAdapter<Item> = items()

    /**
     * @return the filter used to filter items
     */
    val itemFilter: ItemFilter<*, Item>
        get() = itemAdapter.itemFilter

    /**
     * @return the order of the items within the FastAdapter
     */
    val order: Int
        get() = itemAdapter.order

    /**
     * @return the count of items within this adapter
     */
    val adapterItemCount: Int
        get() = itemAdapter.adapterItemCount


    /**
     * @return the items within this adapter
     */
    val adapterItems: List<Item>
        get() = itemAdapter.adapterItems

    /**
     * ctor
     */
    init {
        addAdapter<IAdapter<Item>>(0, itemAdapter)
        cacheSizes()
    }

    /**
     * defines if the IdDistributor is used to provide an ID to all added items which do not yet define an id
     *
     * @param useIdDistributor false if the IdDistributor shouldn't be used
     * @return this
     */
    fun withUseIdDistributor(useIdDistributor: Boolean): FastItemAdapter<Item> {
        itemAdapter.isUseIdDistributor = useIdDistributor
        return this
    }

    /**
     * filters the items with the constraint using the provided Predicate
     *
     * @param constraint the string used to filter the list
     */
    fun filter(constraint: CharSequence) {
        itemAdapter.filter(constraint)
    }

    /**
     * Searches for the given item and calculates its relative position
     *
     * @param item the item which is searched for
     * @return the relative position
     */
    fun getAdapterPosition(item: Item): Int {
        return itemAdapter.getAdapterPosition(item)
    }

    /**
     * returns the global position if the relative position within this adapter was given
     *
     * @param position the relative postion
     * @return the global position
     */
    fun getGlobalPosition(position: Int): Int {
        return itemAdapter.getGlobalPosition(position)
    }

    /**
     * @param position the relative position
     * @return the item inside this adapter
     */
    fun getAdapterItem(position: Int): Item {
        return itemAdapter.getAdapterItem(position)
    }

    /**
     * set a new list of items and apply it to the existing list (clear - add) for this adapter
     *
     * @param items the new items to set
     */
    fun set(items: List<Item>): FastItemAdapter<Item> {
        itemAdapter.set(items)
        return this
    }

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param items the new items to set
     * @return this
     */
    fun setNewList(items: List<Item>): FastItemAdapter<Item> {
        itemAdapter.setNewList(items)
        return this
    }


    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param items        the new items to set
     * @param retainFilter set to true if you want to keep the filter applied
     * @return this
     */
    fun setNewList(items: List<Item>, retainFilter: Boolean): FastItemAdapter<Item> {
        itemAdapter.setNewList(items, retainFilter)
        return this
    }

    /**
     * add an array of items to the end of the existing items
     *
     * @param items the items to add
     */
    @SafeVarargs
    fun add(vararg items: Item): FastItemAdapter<Item> {
        itemAdapter.add(*items)
        return this
    }

    /**
     * add a list of items to the end of the existing items
     *
     * @param items the items to add
     */
    fun add(items: List<Item>): FastItemAdapter<Item> {
        itemAdapter.add(items)
        return this
    }

    /**
     * add an array of items at the given position within the existing items
     *
     * @param position the global position
     * @param items    the items to add
     */
    @SafeVarargs
    fun add(position: Int, vararg items: Item): FastItemAdapter<Item> {
        itemAdapter.add(position, *items)
        return this
    }

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param items    the items to add
     */
    fun add(position: Int, items: List<Item>): FastItemAdapter<Item> {
        itemAdapter.add(position, items)
        return this
    }

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param item     the item to set
     */
    operator fun set(position: Int, item: Item): FastItemAdapter<Item> {
        itemAdapter.set(position, item)
        return this
    }

    /**
     * add an item at the end of the existing items
     *
     * @param item the item to add
     */
    fun add(item: Item): FastItemAdapter<Item> {
        itemAdapter.add(item)
        return this
    }

    /**
     * add an item at the given position within the existing icons
     *
     * @param position the global position
     * @param item     the item to add
     */
    fun add(position: Int, item: Item): FastItemAdapter<Item> {
        itemAdapter.add(position, item)
        return this
    }

    /**
     * moves an item within the list from a position to a position
     *
     * @param fromPosition the position global from which we want to move
     * @param toPosition   the global position to which to move
     * @return this
     */
    fun move(fromPosition: Int, toPosition: Int): FastItemAdapter<Item> {
        itemAdapter.move(fromPosition, toPosition)
        return this
    }

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the global position
     */
    fun remove(position: Int): FastItemAdapter<Item> {
        itemAdapter.remove(position)
        return this
    }

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount the count of items removed
     */
    fun removeItemRange(position: Int, itemCount: Int): FastItemAdapter<Item> {
        itemAdapter.removeRange(position, itemCount)
        return this
    }

    /**
     * removes all items of this adapter
     */
    fun clear(): FastItemAdapter<Item> {
        itemAdapter.clear()
        return this
    }

    /**
     * convenient functions, to force to remap all possible types for the RecyclerView
     */
    fun remapMappedTypes() {
        itemAdapter.remapMappedTypes()
    }
}
