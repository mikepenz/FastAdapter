package com.mikepenz.fastadapter.utils

import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapterNotifier
import java.util.*
import kotlin.math.min

/**
 * The default item list implementation
 */
open class DefaultItemListImpl<Item : GenericItem> @JvmOverloads constructor(
        @Suppress("ConstructorParameterNaming")
        protected var _items: MutableList<Item> = ArrayList()
) : DefaultItemList<Item>() {

    override val items: MutableList<Item>
        get() = _items

    override val isEmpty: Boolean
        get() = _items.isEmpty()

    override fun get(position: Int): Item {
        return _items[position]
    }

    override fun getAdapterPosition(identifier: Long): Int =
            _items.indexOfFirst { it.identifier == identifier }

    override fun remove(position: Int, preItemCount: Int) {
        _items.removeAt(position - preItemCount)
        fastAdapter?.notifyAdapterItemRemoved(position)
    }

    override fun removeRange(position: Int, itemCount: Int, preItemCount: Int) {
        //global position to relative
        val length = _items.size
        //make sure we do not delete too many items
        val saveItemCount = min(itemCount, length - position + preItemCount)
        for (i in 0 until saveItemCount) {
            _items.removeAt(position - preItemCount)
        }
        fastAdapter?.notifyAdapterItemRangeRemoved(position, saveItemCount)
    }

    override fun move(fromPosition: Int, toPosition: Int, preItemCount: Int) {
        val item = _items[fromPosition - preItemCount]
        _items.removeAt(fromPosition - preItemCount)
        _items.add(toPosition - preItemCount, item)
        fastAdapter?.notifyAdapterItemMoved(fromPosition, toPosition)
    }

    override fun size(): Int {
        return _items.size
    }

    override fun clear(preItemCount: Int) {
        val size = _items.size
        _items.clear()
        fastAdapter?.notifyAdapterItemRangeRemoved(preItemCount, size)
    }

    override fun set(position: Int, item: Item, preItemCount: Int) {
        _items[position - preItemCount] = item
        fastAdapter?.notifyAdapterItemChanged(position)
    }

    override fun addAll(items: List<Item>, preItemCount: Int) {
        val countBefore = _items.size
        _items.addAll(items)
        fastAdapter?.notifyAdapterItemRangeInserted(preItemCount + countBefore, items.size)
    }

    override fun addAll(position: Int, items: List<Item>, preItemCount: Int) {
        _items.addAll(position - preItemCount, items)
        fastAdapter?.notifyAdapterItemRangeInserted(position, items.size)
    }

    override fun set(items: List<Item>, preItemCount: Int, adapterNotifier: IAdapterNotifier?) {
        //get sizes
        val newItemsCount = items.size
        val previousItemsCount = _items.size

        //make sure the new items list is not a reference of the already mItems list
        if (items !== _items) {
            //remove all previous items
            if (_items.isNotEmpty()) {
                _items.clear()
            }

            //add all new items to the list
            _items.addAll(items)
        }
        fastAdapter?.let { fastAdapter ->
            //now properly notify the adapter about the changes
            (adapterNotifier ?: IAdapterNotifier.DEFAULT).notify(
                    fastAdapter,
                    newItemsCount,
                    previousItemsCount,
                    preItemCount
            )
        }
    }

    override fun setNewList(items: List<Item>, notify: Boolean) {
        _items = ArrayList(items)
        if (notify) {
            fastAdapter?.notifyAdapterDataSetChanged()
        }
    }
}
