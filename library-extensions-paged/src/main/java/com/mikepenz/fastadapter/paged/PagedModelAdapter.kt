package com.mikepenz.fastadapter.paged

import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListUpdateCallback
import com.mikepenz.fastadapter.*
import com.mikepenz.fastadapter.dsl.FastAdapterDsl
import com.mikepenz.fastadapter.paged.PagedItemListImpl.Companion.getDefaultPlaceholderInterceptor
import com.mikepenz.fastadapter.utils.DefaultItemList

// Notify user that the DSL is currently experimental
@Experimental(level = Experimental.Level.WARNING)
annotation class ExperimentalPagedSupport

/**
 * Kotlin type alias to simplify usage for an all accepting ModelAdapter
 */
typealias GenericPagedModelAdapter<Model> = PagedModelAdapter<Model, GenericItem>

/**
 * Created by mikepenz on 27.12.15.
 * A general ItemAdapter implementation based on the AbstractAdapter to speed up development for general items
 */
@ExperimentalPagedSupport
@FastAdapterDsl
open class PagedModelAdapter<Model, Item : GenericItem>(
        asyncDifferConfig: AsyncDifferConfig<Model>,
        placeholderInterceptor: (position: Int) -> Item = getDefaultPlaceholderInterceptor(),
        var interceptor: (element: Model) -> Item?
) : AbstractAdapter<Item>(), IItemAdapter<Model, Item>, ListUpdateCallback {
    val itemList: PagedItemListImpl<Model, Item> = PagedItemListImpl(this, asyncDifferConfig, placeholderInterceptor, interceptor)

    override var idDistributor: IIdDistributor<Item> = IIdDistributor.DEFAULT as IIdDistributor<Item>

    private val pagedListListener = AsyncPagedListDiffer.PagedListListener<Model> { previousList, currentList ->
        this@PagedModelAdapter.onCurrentListChanged(previousList, currentList)
    }

    init {
        itemList.addPagedListListener(pagedListListener)
        itemList.idDistributor = idDistributor
    }

    override var fastAdapter: FastAdapter<Item>?
        get() = super.fastAdapter
        set(fastAdapter) {
            (itemList as DefaultItemList<Item>).fastAdapter = fastAdapter
            super.fastAdapter = fastAdapter
        }

    /**
     * Defines if the DefaultIdDistributor is used to provide an ID to all added items which do not yet define an id
     */
    var isUseIdDistributor: Boolean
        get() = itemList.isUseIdDistributor
        set(value) {
            itemList.isUseIdDistributor = value
        }

    /**
     * The ModelAdapter does not keep a list of input model's to get retrieve them a `reverseInterceptor` is required
     * usually it is used to get the `Model` from a `IModelItem`
     *
     * @return a List of initial Model's
     */
    open val models: List<Model>
        get() {
            return itemList.differ.currentList?.toList() ?: emptyList()
        }

    /**
     * @return the count of items within this adapter
     */
    override val adapterItemCount: Int
        get() = itemList.size()

    /**
     * @return the items within this adapter
     */
    override val adapterItems: MutableList<Item>
        get() = itemList.items

    /**
     * Generates a [Item] based on it's [Model] using the interceptor
     *
     * @param model the [Model] which will be used to create the [Item]
     * @return the generated [Item]
     */
    open fun intercept(model: Model): Item? {
        return interceptor.invoke(model)
    }

    /**
     * Generates a [List] of [Item] based on it's [List] of [Model] using the interceptor
     *
     * @param models the [List] of [Model] which will be used to create the [List] of [Item]
     * @return the generated [List] of [Item]
     */
    open fun intercept(models: List<Model>): List<Item> = models.mapNotNull { intercept(it) }

    /**
     * Filters the items with the constraint using the provided Predicate
     *
     * @param constraint the string used to filter the list
     */
    override fun filter(constraint: CharSequence?) {
        throw UnsupportedOperationException("Not supported")
    }

    /**
     * Searches for the given item and calculates its relative position
     *
     * @param item the item which is searched for
     * @return the relative position
     */
    override fun getAdapterPosition(item: Item): Int {
        return getAdapterPosition(item.identifier)
    }

    /**
     * Searches for the given identifier and calculates its relative position
     *
     * @param identifier the identifier of an item which is searched for
     * @return the relative position
     */
    override fun getAdapterPosition(identifier: Long): Int {
        return itemList.getAdapterPosition(identifier)
    }

    /**
     * Returns the global position if the relative position within this adapter was given
     *
     * @param position the relative position
     * @return the global position
     */
    override fun getGlobalPosition(position: Int): Int {
        return position + (fastAdapter?.getPreItemCountByOrder(order) ?: 0)
    }

    /**
     * @param position the relative position
     * @return the item inside this adapter
     */
    override fun getAdapterItem(position: Int): Item {
        return itemList[position]
    }

    /**
     * @param position the relative position
     * @return the item at the given relative position within this adapter if it has been loaded
     */
    override fun peekAdapterItem(position: Int): Item? {
        return itemList.peek(position)
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun set(items: List<Model>): PagedModelAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    open operator fun set(list: List<Model>, resetFilter: Boolean, adapterNotifier: IAdapterNotifier?): PagedModelAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun setNewList(items: List<Model>, retainFilter: Boolean): PagedModelAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    @SafeVarargs
    override fun add(vararg items: Model): PagedModelAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun add(items: List<Model>): PagedModelAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    @SafeVarargs
    override fun add(position: Int, vararg items: Model): PagedModelAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun add(position: Int, items: List<Model>): PagedModelAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun set(position: Int, item: Model): PagedModelAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun move(fromPosition: Int, toPosition: Int): PagedModelAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun remove(position: Int): PagedModelAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun removeRange(position: Int, itemCount: Int): PagedModelAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun addInternal(items: List<Item>): IItemAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun addInternal(position: Int, items: List<Item>): IItemAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun setInternal(position: Int, item: Item): IItemAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    /** Managed by the [PagedList] not supported to be managed via the [PagedModelAdapter] */
    override fun clear(): PagedModelAdapter<Model, Item> {
        throw UnsupportedOperationException("Not supported")
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        fastAdapter?.notifyAdapterItemRangeChanged(position, count, payload)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        fastAdapter?.notifyAdapterItemMoved(fromPosition, toPosition)
    }

    override fun onInserted(position: Int, count: Int) {
        fastAdapter?.notifyAdapterItemRangeInserted(position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        fastAdapter?.notifyAdapterItemRangeRemoved(position, count)
    }

    /**
     * Called when the current PagedList is updated.
     *
     * This may be dispatched as part of [submitList] if a background diff isn't
     * needed (such as when the first list is passed, or the list is cleared). In either case,
     * PagedListAdapter will simply call
     * [notifyItemRangeInserted/Removed(0, previousSize)][FastAdapter.notifyItemRangeInserted].
     *
     * This method will *not* be called when the Adapter switches from presenting a [PagedList]
     * to a snapshot version of the [PagedList] during a diff. This means you cannot observe each
     * [PagedList] via this method.
     *
     * @param previousList PagedList that was previously displayed, may be null.
     * @param currentList new PagedList being displayed, may be null.
     *
     * @see getCurrentList
     */
    open fun onCurrentListChanged(previousList: PagedList<Model>?, currentList: PagedList<Model>?) {
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
        itemList.submitList(pagedList)
    }

    /**
     * Set the new list to be displayed.
     *
     * If a list is already being displayed, a diff will be computed on a background thread, which
     * will dispatch Adapter.notifyItem events on the main thread.
     *
     * The commit callback can be used to know when the [PagedList] is committed, but note that it
     * may not be executed. If [PagedList] B is submitted immediately after [PagedList] A, and is
     * committed directly, the callback associated with [PagedList] A will not be run.
     *
     * @param pagedList The new list to be displayed.
     * @param commitCallback Optional runnable that is executed when the [PagedList] is committed, if
     * it is committed.
     */
    fun submitList(pagedList: PagedList<Model>?, commitCallback: Runnable?) {
        itemList.submitList(pagedList, commitCallback)
    }

    /**
     * Get the item from the current PagedList at the specified index.
     *
     * Note that this operates on both loaded items and null padding within the [PagedList].
     *
     * @param position Index of item to get, must be >= 0, and < [adapterItemCount].
     * @return The item, or null, if a null placeholder is at the specified position.
     */
    protected fun getItem(position: Int): Model? {
        return itemList.getItem(position)
    }

    /**
     * Returns the [PagedList] currently being displayed by the Adapter.
     *
     * This is not necessarily the most recent list passed to [submitList],
     * because a diff is computed asynchronously between the new list and the current list before
     * updating the currentList value. May be null if no PagedList is being presented.
     *
     * @return The list currently being displayed.
     *
     * @see onCurrentListChanged
     */
    fun getCurrentList(): PagedList<Model>? {
        return itemList.getCurrentList()
    }

    companion object {

        /**
         * Static method to retrieve a new `ItemAdapter`
         *
         * @return a new ItemAdapter
         */
        @JvmStatic
        fun <Model, Item : GenericItem> models(asyncDifferConfig: AsyncDifferConfig<Model>, placeholderInterceptor: (position: Int) -> Item, interceptor: (element: Model) -> Item?): PagedModelAdapter<Model, Item> {
            return PagedModelAdapter(asyncDifferConfig, placeholderInterceptor, interceptor)
        }
    }
}
