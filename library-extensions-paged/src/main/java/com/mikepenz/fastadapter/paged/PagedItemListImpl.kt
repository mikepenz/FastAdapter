package com.mikepenz.fastadapter.paged

import android.util.Log
import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListUpdateCallback
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapterNotifier
import com.mikepenz.fastadapter.IIdDistributor
import com.mikepenz.fastadapter.utils.DefaultItemList

/**
 * A item list implementation to support the PagedList from the `androidx.paging:paging-runtime` jetpack library
 */
@ExperimentalPagedSupport
open class PagedItemListImpl<Model, Item : GenericItem>(
        listUpdateCallback: ListUpdateCallback,
        differConfig: AsyncDifferConfig<Model>,
        var placeholderInterceptor: (position: Int) -> Item = getDefaultPlaceholderInterceptor(),
        var interceptor: (element: Model) -> Item?
) : DefaultItemList<Item>() {
    val differ: AsyncPagedListDiffer<Model> = AsyncPagedListDiffer<Model>(listUpdateCallback, differConfig)

    var idDistributor: IIdDistributor<Item> = IIdDistributor.DEFAULT as IIdDistributor<Item>

    private val cache: HashMap<Model, Item> = hashMapOf()

    /**
     * Defines if the DefaultIdDistributor is used to provide an ID to all added items which do not yet define an id
     */
    var isUseIdDistributor = true

    override val items: MutableList<Item>
        get() = differ.currentList?.mapNotNull { getItem(it) }?.toMutableList() ?: mutableListOf()// Note this is not efficient

    override val isEmpty: Boolean
        get() = differ.currentList?.isEmpty() == true

    override fun get(position: Int): Item {
        return differ.getItem(position)?.let { getItem(it) } ?: run {
            Log.w(TAG, "Position currently contains a placeholder")
            placeholderInterceptor.invoke(position)
        }
    }

    override fun peek(position: Int): Item? {
        return if (differ.itemCount > position) {
            differ.currentList?.subList(position, position + 1)?.first()?.let { cache[it] }
        } else {
            null
        }
    }

    private fun getItem(model: Model): Item? {
        return cache[model] ?: run {
            return interceptor.invoke(model)?.let {
                if (isUseIdDistributor) {
                    idDistributor.checkId(it)
                }
                cache[model] = it
                it
            }
        }
    }

    override fun getAdapterPosition(identifier: Long): Int = differ.currentList?.indexOfFirst { getItem(it)?.identifier == identifier }
            ?: throw RuntimeException("No item found at position")

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun remove(position: Int, preItemCount: Int) {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun removeRange(position: Int, itemCount: Int, preItemCount: Int) {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun move(fromPosition: Int, toPosition: Int, preItemCount: Int) {
        throw UnsupportedOperationException("Not supported")
    }

    override fun size(): Int {
        return differ.currentList?.size ?: 0
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun clear(preItemCount: Int) {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun set(position: Int, item: Item, preItemCount: Int) {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun addAll(items: List<Item>, preItemCount: Int) {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun addAll(position: Int, items: List<Item>, preItemCount: Int) {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun set(items: List<Item>, preItemCount: Int, adapterNotifier: IAdapterNotifier?) {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun setNewList(items: List<Item>, notify: Boolean) {
        throw UnsupportedOperationException("Not supported")
    }

    /**
     * Set the new list to be displayed.
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
     * If a list is already being displayed, a diff will be computed on a background thread, which
     * will dispatch Adapter.notifyItem events on the main thread.
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

    /**
     * Get the item from the current PagedList at the specified index.
     *
     * Note that this operates on both loaded items and null padding within the PagedList.
     *
     * @param position Index of item to get, must be >= 0, and < {@link #getItemCount()}.
     * @return The item, or null, if a null placeholder is at the specified position.
     */
    fun getItem(position: Int): Model? {
        return differ.getItem(position)
    }

    /**
     * Returns the PagedList currently being displayed by the Adapter.
     *
     * This is not necessarily the most recent list passed to [.submitList],
     * because a diff is computed asynchronously between the new list and the current list before
     * updating the currentList value. May be null if no PagedList is being presented.
     *
     * @return The list currently being displayed.
     *
     * @see AsyncPagedListDiffer.onCurrentListChanged
     */
    fun getCurrentList(): PagedList<Model>? {
        return differ.currentList
    }

    /**
     * Add a PagedListListener to receive updates when the current PagedList changes.
     *
     * @param listener Listener to receive updates.
     *
     * @see getCurrentList
     * @see removePagedListListener
     */
    fun addPagedListListener(listener: AsyncPagedListDiffer.PagedListListener<Model>) {
        differ.addPagedListListener(listener)
    }

    /**
     * Removes a PagedListListener to receive updates when the current PagedList changes.
     *
     * @param listener Listener to receive updates.
     *
     * @see getCurrentList
     * @see addPagedListListener
     */
    fun removePagedListListener(listener: AsyncPagedListDiffer.PagedListListener<Model>) {
        differ.removePagedListListener(listener)
    }

    companion object {
        private const val TAG = "PagedItemListImpl"

        /**
         * Returns the default placeholder interceptor
         *
         * Note if your PagedItemList should contain placeholder, you have to provide a logic what the adapter should show while in placeholding state
         */
        internal fun <Item : GenericItem> getDefaultPlaceholderInterceptor(): (Int) -> Item {
            return { throw RuntimeException("No item found at position") }
        }
    }
}
