package com.mikepenz.fastadapter.paged

import androidx.paging.AsyncPagedListDiffer
import androidx.paging.PagedList
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListUpdateCallback
import com.mikepenz.fastadapter.*
import com.mikepenz.fastadapter.dsl.FastAdapterDsl
import com.mikepenz.fastadapter.utils.AdapterPredicate
import com.mikepenz.fastadapter.utils.DefaultItemList
import com.mikepenz.fastadapter.utils.Triple
import java.util.*
import java.util.Arrays.asList

/**
 * Kotlin type alias to simplify usage for an all accepting ModelAdapter
 */
typealias GenericPagedModelAdapter<Model> = PagedModelAdapter<Model, GenericItem>

/**
 * Created by mikepenz on 27.12.15.
 * A general ItemAdapter implementation based on the AbstractAdapter to speed up development for general items
 */
@FastAdapterDsl
open class PagedModelAdapter<Model, Item : GenericItem>(
        private val asyncDifferConfig: AsyncDifferConfig<Model>,
        var interceptor: (element: Model) -> Item?
) : AbstractAdapter<Item>(), IItemAdapter<Model, Item>, ListUpdateCallback {
    val itemList: PagedItemListImpl<Model, Item> = PagedItemListImpl(this, asyncDifferConfig, interceptor)

    private val pagedListListener = AsyncPagedListDiffer.PagedListListener<Model> { previousList, currentList ->
        this@PagedModelAdapter.onCurrentListChanged(previousList, currentList)
    }

    init {
        itemList.addPagedListListener(pagedListListener)
    }

    override var fastAdapter: FastAdapter<Item>?
        get() = super.fastAdapter
        set(fastAdapter) {
            if (itemList is DefaultItemList<*>) {
                (itemList as DefaultItemList<Item>).fastAdapter = fastAdapter
            }
            super.fastAdapter = fastAdapter
        }

    open var reverseInterceptor: ((element: Item) -> Model?)? = null

    override var idDistributor: IIdDistributor<Item> = IIdDistributor.DEFAULT as IIdDistributor<Item>

    /**
     * Defines if the DefaultIdDistributor is used to provide an ID to all added items which do not yet define an id
     */
    var isUseIdDistributor = true

    /**
     * the ModelAdapter does not keep a list of input model's to get retrieve them a `reverseInterceptor` is required
     * usually it is used to get the `Model` from a `IModelItem`
     *
     * @return a List of initial Model's
     */
    open val models: List<Model>
        get() {
            val list = ArrayList<Model>(itemList.size())
            for (item in itemList.items) {
                when {
                    item is IModelItem<*, *> -> {
                        ((item as IModelItem<*, *>).model as? Model)?.let { model ->
                            list.add(model)
                        }
                    }
                    reverseInterceptor != null -> {
                        reverseInterceptor?.invoke(item)?.let { interceptedItem ->
                            list.add(interceptedItem)
                        }
                    }
                    else -> throw RuntimeException("to get the list of models, the item either needs to implement `IModelItem` or you have to provide a `reverseInterceptor`")
                }
            }
            return list
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
     * Generates a `Item` based on it's `Model` using the interceptor
     *
     * @param model the `Model` which will be used to create the `Item`
     * @return the generated `Item`
     */
    open fun intercept(model: Model): Item? {
        return interceptor.invoke(model)
    }

    /**
     * Generates a List of Item based on it's List of Model using the interceptor
     *
     * @param models the List of Model which will be used to create the List of Item
     * @return the generated List of Item
     */
    open fun intercept(models: List<Model>): List<Item> =
            models.mapNotNull { intercept(it) }

    /**
     * filters the items with the constraint using the provided Predicate
     *
     * @param constraint the string used to filter the list
     */
    override fun filter(constraint: CharSequence?) {
        TODO("Not supported")
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
     * returns the global position if the relative position within this adapter was given
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
     * set a new list of items and apply it to the existing list (clear - add) for this adapter
     * NOTE may consider using setNewList if the items list is a reference to the list which is used inside the adapter
     *
     * @param items the items to set
     */
    override fun set(items: List<Model>): PagedModelAdapter<Model, Item> {
        return set(items, true)
    }

    protected operator fun set(list: List<Model>, resetFilter: Boolean): PagedModelAdapter<Model, Item> {
        val items = intercept(list)
        return setInternal(items, resetFilter, null)
    }

    /**
     * set a new list of model items and apply it to the existing list (clear - add) for this adapter
     * NOTE may consider using setNewList if the items list is a reference to the list which is used inside the adapter
     *
     * @param list            the items to set
     * @param resetFilter     `true` if the filter should get reset
     * @param adapterNotifier a `IAdapterNotifier` allowing to modify the notify logic for the adapter (keep null for default)
     * @return this
     */
    open operator fun set(
            list: List<Model>,
            resetFilter: Boolean,
            adapterNotifier: IAdapterNotifier?
    ): PagedModelAdapter<Model, Item> {
        val items = intercept(list)
        return setInternal(items, resetFilter, adapterNotifier)
    }

    /**
     * set a new list of model and apply it to the existing list (clear - add) for this adapter
     * NOTE may consider using setNewList if the items list is a reference to the list which is used inside the adapter
     *
     * @param items           the items to set
     * @param resetFilter     `true` if the filter should get reset
     * @param adapterNotifier a `IAdapterNotifier` allowing to modify the notify logic for the adapter (keep null for default)
     * @return this
     */
    open fun setInternal(
            items: List<Item>,
            resetFilter: Boolean,
            adapterNotifier: IAdapterNotifier?
    ): PagedModelAdapter<Model, Item> {
        if (isUseIdDistributor) {
            idDistributor.checkIds(items)
        }

        fastAdapter?.extensions?.forEach { extension ->
            extension[items] = resetFilter
        }

        //map the types
        mapPossibleTypes(items)

        //forward set
        val itemsBeforeThisAdapter = fastAdapter?.getPreItemCountByOrder(order) ?: 0
        itemList[items, itemsBeforeThisAdapter] = adapterNotifier

        return this
    }

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param items         the new items to set
     * @param retainFilter set to true if you want to keep the filter applied
     * @return this
     */
    override fun setNewList(items: List<Model>, retainFilter: Boolean): PagedModelAdapter<Model, Item> {
        val newItems = intercept(items)

        if (isUseIdDistributor) {
            idDistributor.checkIds(newItems)
        }

        mapPossibleTypes(newItems)

        itemList.setNewList(newItems, true)

        return this
    }

    /**
     * forces to remap all possible types for the RecyclerView
     */
    open fun remapMappedTypes() {
        fastAdapter?.clearTypeInstance()
        mapPossibleTypes(itemList.items)
    }

    /**
     * add an array of items to the end of the existing items
     *
     * @param items the items to add
     */
    @SafeVarargs
    override fun add(vararg items: Model): PagedModelAdapter<Model, Item> {
        return add(asList(*items))
    }

    /**
     * add a list of items to the end of the existing items
     *
     * @param items the items to add
     */
    override fun add(items: List<Model>): PagedModelAdapter<Model, Item> {
        return addInternal(intercept(items))
    }

    override fun addInternal(items: List<Item>): PagedModelAdapter<Model, Item> {
        if (isUseIdDistributor) {
            idDistributor.checkIds(items)
        }
        val fastAdapter = fastAdapter
        if (fastAdapter != null) {
            itemList.addAll(items, fastAdapter.getPreItemCountByOrder(order))
        } else {
            itemList.addAll(items, 0)
        }
        mapPossibleTypes(items)
        return this
    }

    /**
     * add an array of items at the given position within the existing items
     *
     * @param position the global position
     * @param items    the items to add
     */
    @SafeVarargs
    override fun add(position: Int, vararg items: Model): PagedModelAdapter<Model, Item> {
        return add(position, asList(*items))
    }

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param items     the items to add
     */
    override fun add(position: Int, items: List<Model>): PagedModelAdapter<Model, Item> {
        val interceptedItems = intercept(items)
        return addInternal(position, interceptedItems)
    }

    override fun addInternal(position: Int, items: List<Item>): PagedModelAdapter<Model, Item> {
        if (isUseIdDistributor) {
            idDistributor.checkIds(items)
        }
        if (items.isNotEmpty()) {
            itemList.addAll(position, items, fastAdapter?.getPreItemCountByOrder(order) ?: 0)
            mapPossibleTypes(items)
        }
        return this
    }

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param item  the item to set
     */
    override fun set(position: Int, item: Model): PagedModelAdapter<Model, Item> {
        val interceptedItem = intercept(item) ?: return this
        return setInternal(position, interceptedItem)
    }

    override fun setInternal(position: Int, item: Item): PagedModelAdapter<Model, Item> {
        if (isUseIdDistributor) {
            idDistributor.checkId(item)
        }
        itemList[position, item] = fastAdapter?.getPreItemCount(position) ?: 0
        fastAdapter?.registerTypeInstance(item)
        return this
    }

    /**
     * moves an item within the list from a position to a position
     *
     * @param fromPosition the position global from which we want to move
     * @param toPosition   the global position to which to move
     * @return this
     */
    override fun move(fromPosition: Int, toPosition: Int): PagedModelAdapter<Model, Item> {
        itemList.move(fromPosition, toPosition, fastAdapter?.getPreItemCount(fromPosition) ?: 0)
        return this
    }

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the global position
     */
    override fun remove(position: Int): PagedModelAdapter<Model, Item> {
        itemList.remove(position, fastAdapter?.getPreItemCount(position) ?: 0)
        return this
    }

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount the count of items which were removed
     */
    override fun removeRange(position: Int, itemCount: Int): PagedModelAdapter<Model, Item> {
        itemList.removeRange(position, itemCount, fastAdapter?.getPreItemCount(position) ?: 0)
        return this
    }

    /**
     * removes all items of this adapter
     */
    override fun clear(): PagedModelAdapter<Model, Item> {
        itemList.clear(fastAdapter?.getPreItemCountByOrder(order) ?: 0)
        return this
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
     *
     * This may be dispatched as part of [.submitList] if a background diff isn't
     * needed (such as when the first list is passed, or the list is cleared). In either case,
     * PagedListAdapter will simply call
     * [notifyItemRangeInserted/Removed(0, mPreviousSize)][.notifyItemRangeInserted].
     *
     *
     * This method will *not*be called when the Adapter switches from presenting a PagedList
     * to a snapshot version of the PagedList during a diff. This means you cannot observe each
     * PagedList via this method.
     *
     * @param previousList PagedList that was previously displayed, may be null.
     * @param currentList new PagedList being displayed, may be null.
     *
     * @see .getCurrentList
     */
    open fun onCurrentListChanged(previousList: PagedList<Model>?, currentList: PagedList<Model>?) {
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
    fun submitList(pagedList: PagedList<Model>) {
        itemList.submitList(pagedList)
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
        itemList.submitList(pagedList, commitCallback)
    }

    protected fun getItem(position: Int): Model? {
        return itemList.getItem(position)
    }

    /**
     * Returns the PagedList currently being displayed by the Adapter.
     *
     *
     * This is not necessarily the most recent list passed to [.submitList],
     * because a diff is computed asynchronously between the new list and the current list before
     * updating the currentList value. May be null if no PagedList is being presented.
     *
     * @return The list currently being displayed.
     *
     * @see .onCurrentListChanged
     */
    fun getCurrentList(): PagedList<Model>? {
        return itemList.getCurrentList()
    }

    /**
     * remvoes an item by it's identifier
     *
     * @param identifier the identifier to search for
     * @return this
     */
    open fun removeByIdentifier(identifier: Long): PagedModelAdapter<Model, Item> {
        recursive(object : AdapterPredicate<Item> {
            override fun apply(
                    lastParentAdapter: IAdapter<Item>,
                    lastParentPosition: Int,
                    item: Item,
                    position: Int
            ): Boolean {
                if (identifier == item.identifier) {
                    //if it's a subitem remove it from the parent
                    (item as? IExpandable<*>?)?.let { expandable ->
                        //a sub item which is not in the list can be instantly deleted
                        expandable.parent?.subItems?.remove(item)
                    }
                    if (position != -1) {
                        //a normal displayed item can only be deleted afterwards
                        remove(position)
                    }
                }
                return false
            }
        }, false)

        return this
    }

    /**
     * util function which recursively iterates over all items and subItems of the given adapter.
     * It executes the given `predicate` on every item and will either stop if that function returns true, or continue (if stopOnMatch is false)
     *
     * @param predicate   the predicate to run on every item, to check for a match or do some changes (e.g. select)
     * @param stopOnMatch defines if we should stop iterating after the first match
     * @return Triple&lt;Boolean, IItem, Integer&gt; The first value is true (it is always not null), the second contains the item and the third the position (if the item is visible) if we had a match, (always false and null and null in case of stopOnMatch == false)
     */
    open fun recursive(
            predicate: AdapterPredicate<Item>,
            stopOnMatch: Boolean
    ): Triple<Boolean, Item, Int> {
        fastAdapter?.let { fastAdapter ->
            val preItemCount = fastAdapter.getPreItemCountByOrder(order)
            for (i in 0 until adapterItemCount) {
                val globalPosition = i + preItemCount

                //retrieve the item + it's adapter
                val relativeInfo = fastAdapter.getRelativeInfo(globalPosition)
                val item = relativeInfo.item
                if (item != null) {
                    relativeInfo.adapter?.let { adapter ->
                        if (predicate.apply(
                                        adapter,
                                        globalPosition,
                                        item,
                                        globalPosition
                                ) && stopOnMatch
                        ) {
                            return Triple(true, item, globalPosition)
                        }
                    }
                    (item as? IExpandable<*>?)?.let { expandableItem ->
                        relativeInfo.adapter?.let { adapter ->
                            val res = FastAdapter.recursiveSub(
                                    adapter,
                                    globalPosition,
                                    expandableItem,
                                    predicate,
                                    stopOnMatch
                            )
                            if (res.first && stopOnMatch) {
                                return res
                            }
                        }
                    }
                }
            }
        }

        return Triple(false, null, null)
    }

    companion object {

        /**
         * static method to retrieve a new `ItemAdapter`
         *
         * @return a new ItemAdapter
         */
        @JvmStatic
        fun <Model, Item : GenericItem> models(asyncDifferConfig: AsyncDifferConfig<Model>, interceptor: (element: Model) -> Item?): PagedModelAdapter<Model, Item> {
            return PagedModelAdapter(asyncDifferConfig, interceptor)
        }
    }
}
