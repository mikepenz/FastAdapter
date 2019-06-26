package com.mikepenz.fastadapter.adapters

import android.widget.Filter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.listeners.ItemFilterListener
import com.mikepenz.fastadapter.select.SelectExtension
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.HashSet
import kotlin.math.min

/**
 * ItemFilter which extends the Filter api provided by Android
 * This calls automatically all required methods, just overwrite the filterItems method
 */
open class ItemFilter<Model, Item : GenericItem>(private val mItemAdapter: ModelAdapter<Model, Item>) :
        Filter() {
    private var originalItems: MutableList<Item>? = null
    var constraint: CharSequence? = null
        private set


    //the listener which will be called after the items were filtered
    var itemFilterListener: ItemFilterListener<Item>? = null

    //the filter predicate which is used in the ItemFilter
    var filterPredicate: ((item: Item, constraint: CharSequence?) -> Boolean)? = null

    /**
     * helper method to get all selections from the ItemAdapter's original item list
     *
     * @return a Set with the global positions of all selected Items
     */
    open val selections: Set<Int>
        get() {
            val fastAdapter = mItemAdapter.fastAdapter ?: return emptySet()
            val adapterOffset = fastAdapter.getPreItemCountByOrder(mItemAdapter.order)
            return originalItems?.mapIndexedNotNullTo(HashSet()) { index, item -> if (item.isSelected) index + adapterOffset else null }
                    ?: fastAdapter.getExtension<SelectExtension<Item>>(SelectExtension::class.java)?.selections
                    ?: emptySet()
        }

    /**
     * helper method to get all selections from the ItemAdapter's original item list
     *
     * @return a Set with the selected items out of all items in this itemAdapter (not the listed ones)
     */
    open val selectedItems: Set<Item>
        get() {
            return originalItems?.filterTo(HashSet()) { it.isSelected }
                    ?: mItemAdapter.fastAdapter?.getExtension<SelectExtension<Item>>(SelectExtension::class.java)?.selectedItems
                    ?: emptySet()
        }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()

        //return nothing
        if (originalItems == null && constraint.isNullOrEmpty()) {
            return results
        }

        //call extensions
        mItemAdapter.fastAdapter?.extensions?.forEach { adapterExtension ->
            adapterExtension.performFiltering(constraint)
        }

        this.constraint = constraint

        if (originalItems == null) {
            originalItems = ArrayList(mItemAdapter.adapterItems)
        }

        // We implement here the filter logic
        if (constraint.isNullOrEmpty()) {
            // No filter implemented we return all the list
            results.values = originalItems
            results.count = originalItems?.size ?: 0
            //our filter was cleared we can now forget the old OriginalItems
            originalItems = null

            itemFilterListener?.onReset()
        } else {
            // We perform filtering operation
            val filteredItems = filterPredicate?.let { filterPredicate ->
                originalItems?.mapNotNull { item -> item.takeIf { filterPredicate(item, constraint) } }
            } ?: mItemAdapter.adapterItems

            results.values = filteredItems
            results.count = filteredItems.size
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        // Now we have to inform the adapter about the new list filtered
        if (results.values != null) {
            mItemAdapter.setInternal(results.values as List<Item>, false, null)
        }

        //only fire when we are filtered, not in onreset
        if (originalItems != null) {
            itemFilterListener?.itemsFiltered(constraint, results.values as List<Item>)
        }
    }

    fun resetFilter() {
        //reset the filter
        performFiltering(null)
    }

    fun filterItems(filter: CharSequence) {
        publishResults(filter, performFiltering(filter))
    }

    /**
     * Searches for the given item and calculates its relative position
     *
     * @param item the item which is searched for
     * @return the relative position
     */
    fun getAdapterPosition(item: Item): Int {
        return getAdapterPosition(item.identifier)
    }

    /**
     * Searches for the given identifier and calculates its relative position
     *
     * @param identifier the identifier of an item which is searched for
     * @return the relative position
     */
    fun getAdapterPosition(identifier: Long): Int {
        return originalItems?.indexOfFirst { it.identifier == identifier } ?: -1
    }

    /**
     * add an array of items to the end of the existing items
     *
     * @param items the items to add
     */
    @SafeVarargs
    fun add(vararg items: Item): ModelAdapter<*, Item> {
        return add(asList(*items))
    }

    /**
     * add a list of items to the end of the existing items
     * will prior check if we are currently filtering
     *
     * @param items the items to add
     */
    fun add(items: List<Item>): ModelAdapter<*, Item> {
        if (items.isEmpty()) {
            return mItemAdapter
        }
        return originalItems?.let { originalItems ->
            if (mItemAdapter.isUseIdDistributor) {
                mItemAdapter.idDistributor.checkIds(items)
            }
            originalItems.addAll(items)
            publishResults(constraint, performFiltering(constraint))
            mItemAdapter
        } ?: mItemAdapter.addInternal(items)
    }

    /**
     * add an array of items at the given position within the existing items
     *
     * @param position the global position
     * @param items    the items to add
     */
    @SafeVarargs
    fun add(position: Int, vararg items: Item): ModelAdapter<*, Item> {
        return add(position, asList(*items))
    }

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param items    the items to add
     */
    fun add(position: Int, items: List<Item>): ModelAdapter<*, Item> {
        if (items.isEmpty()) {
            return mItemAdapter
        }
        return originalItems?.let { originalItems ->
            if (mItemAdapter.isUseIdDistributor) {
                mItemAdapter.idDistributor.checkIds(items)
            }
            mItemAdapter.fastAdapter?.let { fastAdapter ->
                val origPosition = getAdapterPosition(mItemAdapter.adapterItems[position]) - fastAdapter.getPreItemCount(position)
                originalItems.addAll(origPosition, items)
            }
            publishResults(constraint, performFiltering(constraint))
            mItemAdapter
        } ?: mItemAdapter.addInternal(position, items)
    }

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param item     the item to set
     */
    operator fun set(position: Int, item: Item): ModelAdapter<*, Item> {
        return originalItems?.let { originalItems ->
            if (mItemAdapter.isUseIdDistributor) {
                mItemAdapter.idDistributor.checkId(item)
            }
            mItemAdapter.fastAdapter?.let { fastAdapter ->
                val origPosition = getAdapterPosition(mItemAdapter.adapterItems[position]) - fastAdapter.getPreItemCount(position)
                originalItems[origPosition] = item
            }
            publishResults(constraint, performFiltering(constraint))
            mItemAdapter
        } ?: mItemAdapter.setInternal(position, item)
    }

    /**
     * moves an item within the list from a position to a position
     *
     * @param fromPosition the position global from which we want to move
     * @param toPosition   the global position to which to move
     * @return this
     */
    fun move(fromPosition: Int, toPosition: Int): ModelAdapter<*, Item> {
        return originalItems?.let { originalItems ->
            mItemAdapter.fastAdapter?.getPreItemCount(fromPosition)?.let { preItemCount ->
                val adjustedFrom = getAdapterPosition(mItemAdapter.adapterItems[fromPosition])
                val adjustedTo = getAdapterPosition(mItemAdapter.adapterItems[toPosition])
                val item = originalItems[adjustedFrom - preItemCount]
                originalItems.removeAt(adjustedFrom - preItemCount)
                originalItems.add(adjustedTo - preItemCount, item)
                performFiltering(constraint)
            }
            mItemAdapter
        } ?: mItemAdapter.move(fromPosition, toPosition)
    }

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the global position
     */
    fun remove(position: Int): ModelAdapter<*, Item> {
        return originalItems?.let { originalItems ->
            mItemAdapter.fastAdapter?.let { fastAdapter ->
                val origPosition = getAdapterPosition(mItemAdapter.adapterItems[position]) - fastAdapter.getPreItemCount(position)
                originalItems.removeAt(origPosition)
            }
            publishResults(constraint, performFiltering(constraint))
            mItemAdapter
        } ?: mItemAdapter.remove(position)
    }

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount the count of items which were removed
     */
    fun removeRange(position: Int, itemCount: Int): ModelAdapter<*, Item> {
        return originalItems?.let { originalItems ->
            //global position to relative
            val length = originalItems.size
            mItemAdapter.fastAdapter?.getPreItemCount(position)?.let { preItemCount ->
                //make sure we do not delete to many items
                val saveItemCount = min(itemCount, length - position + preItemCount)
                for (i in 0 until saveItemCount) {
                    originalItems.removeAt(position - preItemCount)
                }
                publishResults(constraint, performFiltering(constraint))
            }
            mItemAdapter
        } ?: mItemAdapter.removeRange(position, itemCount)
    }

    /**
     * removes all items of this adapter
     */
    fun clear(): ModelAdapter<*, Item> {
        return originalItems?.let { originalItems ->
            originalItems.clear()
            publishResults(constraint, performFiltering(constraint))
            mItemAdapter
        } ?: mItemAdapter.clear()
    }
}
