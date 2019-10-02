package com.mikepenz.fastadapter.diff

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IFastAdapter
import com.mikepenz.fastadapter.adapters.ModelAdapter
import com.mikepenz.fastadapter.utils.ComparableItemListImpl
import java.util.*

/**
 * Created by mikepenz on 28.10.16.
 */

object FastAdapterDiffUtil {

    /**
     * This method will compute a [androidx.recyclerview.widget.DiffUtil.DiffResult] based on the given adapter, and the list of new items.
     *
     *
     * It automatically collapses all expandables (if enabled) as they are not supported by the diff util,
     * pre sort the items based on the comparator if available,
     * map the new item types for the FastAdapter then calculates the [androidx.recyclerview.widget.DiffUtil.DiffResult] using the [DiffUtil].
     *
     *
     * As the last step it will replace the items inside the adapter with the new set of items provided.
     *
     * @param adapter     the adapter containing the current items.
     * @param items       the new set of items we want to put into the adapter
     * @param callback    the callback used to implement the required checks to identify changes of items.
     * @param detectMoves configuration for the [DiffUtil.calculateDiff] method
     * @param <A>         The adapter type, whereas A extends [ModelAdapter]
     * @param <Model>     The model type we work with
     * @param <Item>      The item type kept in the adapter
     * @return the [androidx.recyclerview.widget.DiffUtil.DiffResult] computed.
    </Item></Model></A> */
    fun <A : ModelAdapter<Model, Item>, Model, Item : GenericItem> calculateDiff(adapter: A, items: List<Item>, callback: DiffCallback<Item> = DiffCallbackImpl(), detectMoves: Boolean = true): DiffUtil.DiffResult {
        if (adapter.isUseIdDistributor) {
            adapter.idDistributor.checkIds(items)
        }

        // The FastAdapterDiffUtil does not handle expanded items. Call collapse if possible
        collapseIfPossible(adapter.fastAdapter)

        //if we have a comparator then sort
        if (adapter.itemList is ComparableItemListImpl<*>) {
            Collections.sort(items, (adapter.itemList as ComparableItemListImpl<Item>).comparator)
        }

        //map the types
        adapter.mapPossibleTypes(items)

        //remember the old items
        val adapterItems = adapter.adapterItems
        val oldItems = adapterItems.toList()

        //pass in the oldItem list copy as we will update the one in the adapter itself
        val result = DiffUtil.calculateDiff(FastAdapterCallback(oldItems, items, callback), detectMoves)

        //make sure the new items list is not a reference of the already mItems list
        if (items !== adapterItems) {
            //remove all previous items
            if (adapterItems.isNotEmpty()) {
                adapterItems.clear()
            }

            //add all new items to the list
            adapterItems.addAll(items)
        }

        return result
    }

    /**
     * Uses Reflection to collapse all items if this adapter uses expandable items
     *
     * @param fastAdapter
     */
    private fun <Item : GenericItem> collapseIfPossible(fastAdapter: IFastAdapter<Item>?) {
        fastAdapter ?: return
        try {
            val c: Class<IAdapterExtension<Item>> = Class.forName("com.mikepenz.fastadapter.expandable.ExpandableExtension") as Class<IAdapterExtension<Item>>
            val extension = fastAdapter.getExtension(c)
            if (extension != null) {
                val method = extension.javaClass.getMethod("collapse")
                method.invoke(extension)
            }
        } catch (ignored: Exception) {
            //
        }
    }

    /**
     * Dispatches a [androidx.recyclerview.widget.DiffUtil.DiffResult] to the given Adapter.
     *
     * @param adapter the adapter to dispatch the updates to
     * @param result  the computed [androidx.recyclerview.widget.DiffUtil.DiffResult]
     * @return the adapter to allow chaining
     */
    operator fun <A : ModelAdapter<Model, Item>, Model, Item : GenericItem> set(adapter: A, result: DiffUtil.DiffResult): A {
        result.dispatchUpdatesTo(FastAdapterListUpdateCallback(adapter))
        return adapter
    }

    /**
     * convenient function for [calculateDiff]
     *
     * @return the [androidx.recyclerview.widget.DiffUtil.DiffResult] computed.
     */
    fun <A : ModelAdapter<Model, Item>, Model, Item : GenericItem> calculateDiff(adapter: A, items: List<Item>, callback: DiffCallback<Item>): DiffUtil.DiffResult {
        return calculateDiff(adapter, items, callback, true)
    }

    /**
     * convenient function for [calculateDiff]
     *
     * @return the [androidx.recyclerview.widget.DiffUtil.DiffResult] computed.
     */
    fun <A : ModelAdapter<Model, Item>, Model, Item : GenericItem> calculateDiff(adapter: A, items: List<Item>, detectMoves: Boolean): DiffUtil.DiffResult {
        return calculateDiff(adapter, items, DiffCallbackImpl(), detectMoves)
    }

    /**
     * convenient function for [calculateDiff]
     *
     * @return the [androidx.recyclerview.widget.DiffUtil.DiffResult] computed.
     */
    fun <A : ModelAdapter<Model, Item>, Model, Item : GenericItem> calculateDiff(adapter: A, items: List<Item>): DiffUtil.DiffResult {
        return calculateDiff(adapter, items, DiffCallbackImpl(), true)
    }

    /**
     * Calculates a [androidx.recyclerview.widget.DiffUtil.DiffResult] given the adapter and the items, and will directly dispatch them to the adapter.
     *
     * @param adapter     the adapter containing the current items.
     * @param items       the new set of items we want to put into the adapter
     * @param callback    the callback used to implement the required checks to identify changes of items.
     * @param detectMoves configuration for the [DiffUtil.calculateDiff] method
     * @param <A>         The adapter type, whereas A extends [ModelAdapter]
     * @param <Model>     The model type we work with
     * @param <Item>      The item type kept in the adapter
     * @return the adapter to allow chaining
    </Item></Model></A> */
    fun <A : ModelAdapter<Model, Item>, Model, Item : GenericItem> set(adapter: A, items: List<Item>, callback: DiffCallback<Item>, detectMoves: Boolean): A {
        val result = calculateDiff(adapter, items, callback, detectMoves)
        return set(adapter, result)
    }

    /**
     * convenient function for [.set]
     *
     * @return the adapter to allow chaining
     */
    fun <A : ModelAdapter<Model, Item>, Model, Item : GenericItem> set(adapter: A, items: List<Item>, callback: DiffCallback<Item>): A {
        return set(adapter, items, callback, true)
    }

    /**
     * convenient function for [.set]
     *
     * @return the adapter to allow chaining
     */
    fun <A : ModelAdapter<Model, Item>, Model, Item : GenericItem> set(adapter: A, items: List<Item>, detectMoves: Boolean): A {
        return set(adapter, items, DiffCallbackImpl(), detectMoves)
    }

    /**
     * convenient function for [.set]
     *
     * @return the adapter to allow chaining
     */
    operator fun <A : ModelAdapter<Model, Item>, Model, Item : GenericItem> set(adapter: A, items: List<Item>): A {
        return set(adapter, items, DiffCallbackImpl())
    }

    /**
     * Convenient implementation for the [androidx.recyclerview.widget.DiffUtil.Callback] to simplify difference calculation using [FastAdapter] items.
     *
     * @param <Item> the item type in the adapter
    </Item> */
    private class FastAdapterCallback<Item : GenericItem> internal constructor(private val oldItems: List<Item>, private val newItems: List<Item>, private val callback: DiffCallback<Item>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return callback.areItemsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return callback.areContentsTheSame(oldItems[oldItemPosition], newItems[newItemPosition])
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val result = callback.getChangePayload(oldItems[oldItemPosition], oldItemPosition, newItems[newItemPosition], newItemPosition)
            return result ?: super.getChangePayload(oldItemPosition, newItemPosition)
        }
    }

    /**
     * Default implementation of the [ListUpdateCallback] to apply changes to the adapter and notify about the changes.
     */
    private class FastAdapterListUpdateCallback<A : ModelAdapter<Model, Item>, Model, Item : GenericItem> internal constructor(private val adapter: A) : ListUpdateCallback {

        private val preItemCountByOrder: Int
            get() = adapter.fastAdapter?.getPreItemCountByOrder(adapter.order) ?: 0

        override fun onInserted(position: Int, count: Int) {
            adapter.fastAdapter?.notifyAdapterItemRangeInserted(preItemCountByOrder + position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.fastAdapter?.notifyAdapterItemRangeRemoved(preItemCountByOrder + position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.fastAdapter?.notifyAdapterItemMoved(preItemCountByOrder + fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.fastAdapter?.notifyAdapterItemRangeChanged(preItemCountByOrder + position, count, payload)
        }
    }
}
