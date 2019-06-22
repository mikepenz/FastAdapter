package com.mikepenz.fastadapter.utils

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IAdapterNotifier
import com.mikepenz.fastadapter.IItem
import java.util.*

/**
 * The default item list implementation
 */

open class DefaultItemListImpl<Item : IItem<out RecyclerView.ViewHolder>> @JvmOverloads constructor(
        protected var mItems: MutableList<Item> = ArrayList()
) : DefaultItemList<Item>() {

    override val items: MutableList<Item>
        get() = mItems

    override val isEmpty: Boolean
        get() = mItems.isEmpty()

    override fun get(position: Int): Item {
        return mItems[position]
    }

    override fun getAdapterPosition(identifier: Long): Int {
        var i = 0
        val size = mItems.size
        while (i < size) {
            if (mItems[i].identifier == identifier) {
                return i
            }
            i++
        }
        return -1
    }

    override fun remove(position: Int, preItemCount: Int) {
        mItems.removeAt(position - preItemCount)
        fastAdapter?.notifyAdapterItemRemoved(position)
    }

    override fun removeRange(position: Int, itemCount: Int, preItemCount: Int) {
        //global position to relative
        val length = mItems.size
        //make sure we do not delete to many items
        val saveItemCount = Math.min(itemCount, length - position + preItemCount)

        for (i in 0 until saveItemCount) {
            mItems.removeAt(position - preItemCount)
        }
        fastAdapter?.notifyAdapterItemRangeRemoved(position, saveItemCount)
    }

    override fun move(fromPosition: Int, toPosition: Int, preItemCount: Int) {
        val item = mItems[fromPosition - preItemCount]
        mItems.removeAt(fromPosition - preItemCount)
        mItems.add(toPosition - preItemCount, item)
        fastAdapter?.notifyAdapterItemMoved(fromPosition, toPosition)
    }

    override fun size(): Int {
        return mItems.size
    }

    override fun clear(preItemCount: Int) {
        val size = mItems.size
        mItems.clear()
        fastAdapter?.notifyAdapterItemRangeRemoved(preItemCount, size)
    }

    override fun set(position: Int, item: Item, preItemCount: Int) {
        mItems[position - preItemCount] = item
        fastAdapter?.notifyAdapterItemChanged(position)
    }

    override fun addAll(items: List<Item>, preItemCount: Int) {
        val countBefore = mItems.size
        mItems.addAll(items)
        fastAdapter?.notifyAdapterItemRangeInserted(preItemCount + countBefore, items.size)
    }

    override fun addAll(position: Int, items: List<Item>, preItemCount: Int) {
        mItems.addAll(position - preItemCount, items)
        fastAdapter?.notifyAdapterItemRangeInserted(position, items.size)
    }

    override fun set(items: List<Item>, preItemCount: Int, adapterNotifier: IAdapterNotifier?) {
        //get sizes
        val newItemsCount = items.size
        val previousItemsCount = mItems.size

        //make sure the new items list is not a reference of the already mItems list
        if (items !== mItems) {
            //remove all previous items
            if (mItems.isNotEmpty()) {
                mItems.clear()
            }

            //add all new items to the list
            mItems.addAll(items)
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
        mItems = ArrayList(items)
        if (notify) {
            fastAdapter?.notifyAdapterDataSetChanged()
        }
    }
}
