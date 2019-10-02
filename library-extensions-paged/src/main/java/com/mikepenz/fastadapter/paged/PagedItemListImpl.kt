package com.mikepenz.fastadapter.paged

import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListUpdateCallback
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapterNotifier
import com.mikepenz.fastadapter.utils.DefaultItemList
import kotlin.math.min

/**
 * The default item list implementation
 */

open class PagedItemListImpl<Model, Item : GenericItem> @JvmOverloads constructor(
        internal val listUpdateCallback: ListUpdateCallback,
        internal var differConfig: AsyncDifferConfig<Model>,
        var interceptor: (element: Model) -> Item?
) : DefaultItemList<Item>() {
    val differ: AsyncPagedListDiffer<Model> = AsyncPagedListDiffer<Model>(listUpdateCallback, differConfig)

    override val items: MutableList<Item>
        get() = differ.currentList!!.mapNotNull { interceptor.invoke(it) }.toMutableList() // Note this is not efficient

    override val isEmpty: Boolean
        get() = differ.currentList!!.isEmpty()

    override fun get(position: Int): Item {
        return interceptor.invoke(differ.currentList!![position]!!)!!
    }

    override fun getAdapterPosition(identifier: Long): Int =
            differ.currentList!!.indexOfFirst { interceptor.invoke(it)?.identifier == identifier }

    override fun remove(position: Int, preItemCount: Int) {
        differ.currentList!!.removeAt(position - preItemCount)
        fastAdapter?.notifyAdapterItemRemoved(position)
    }

    override fun removeRange(position: Int, itemCount: Int, preItemCount: Int) {
        //global position to relative
        val length = differ.currentList!!.size
        //make sure we do not delete too many items
        val saveItemCount = min(itemCount, length - position + preItemCount)
        for (i in 0 until saveItemCount) {
            differ.currentList!!.removeAt(position - preItemCount)
        }
        fastAdapter?.notifyAdapterItemRangeRemoved(position, saveItemCount)
    }

    override fun move(fromPosition: Int, toPosition: Int, preItemCount: Int) {
        val item = differ.currentList!![fromPosition - preItemCount]
        differ.currentList!!.removeAt(fromPosition - preItemCount)
        differ.currentList!!.add(toPosition - preItemCount, item)
        fastAdapter?.notifyAdapterItemMoved(fromPosition, toPosition)
    }

    override fun size(): Int {
        return differ.currentList?.size ?: 0
    }

    override fun clear(preItemCount: Int) {
        TODO("Not supported")
    }

    override fun set(position: Int, item: Item, preItemCount: Int) {
        TODO("Not supported")
    }

    override fun addAll(items: List<Item>, preItemCount: Int) {
        TODO("Not supported")
    }

    override fun addAll(position: Int, items: List<Item>, preItemCount: Int) {
        TODO("Not supported")
    }

    override fun set(items: List<Item>, preItemCount: Int, adapterNotifier: IAdapterNotifier?) {
        TODO("Not supported")
    }

    override fun setNewList(items: List<Item>, notify: Boolean) {
        // TODO
        // mItems = ArrayList(items)
        if (notify) {
            fastAdapter?.notifyAdapterDataSetChanged()
        }
    }


    /**
     * Set the new list to be displayed.
     *
     *
     * If a list is already being displayed, a diff will be computed on a background thread, which
     * will dispatch Adapter.notifyItem events on the main thread.
     *
     * @param pagedList The new list to be displayed.
     */
    fun submitList(pagedList: PagedList<Model>?) {
        differ.submitList(pagedList)
    }

    /**
     * Set the new list to be displayed.
     *
     *
     * If a list is already being displayed, a diff will be computed on a background thread, which
     * will dispatch Adapter.notifyItem events on the main thread.
     *
     *
     * The commit callback can be used to know when the PagedList is committed, but note that it
     * may not be executed. If PagedList B is submitted immediately after PagedList A, and is
     * committed directly, the callback associated with PagedList A will not be run.
     *
     * @param pagedList The new list to be displayed.
     * @param commitCallback Optional runnable that is executed when the PagedList is committed, if
     * it is committed.
     */
    fun submitList(pagedList: PagedList<Model>?, commitCallback: Runnable?) {
        differ.submitList(pagedList, commitCallback)
    }

    fun getItem(position: Int): Model? {
        return differ.getItem(position)
    }

    fun getCurrentList(): PagedList<Model>? {
        return differ.currentList
    }

    fun addPagedListListener(listener: AsyncPagedListDiffer.PagedListListener<Model>) {
        differ.addPagedListListener(listener)
    }
}
