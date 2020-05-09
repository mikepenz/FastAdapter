package com.mikepenz.fastadapter

import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.adapters.ItemAdapter.Companion.items
import com.mikepenz.fastadapter.dsl.FastAdapterDsl
import com.mikepenz.fastadapter.extensions.ExtensionsFactories
import com.mikepenz.fastadapter.listeners.*
import com.mikepenz.fastadapter.utils.AdapterPredicate
import com.mikepenz.fastadapter.utils.DefaultItemVHFactoryCache
import com.mikepenz.fastadapter.utils.Triple
import com.mikepenz.fastadapter.utils.attachToView
import java.util.*
import kotlin.math.min

/**
 * Kotlin type alias to simplify usage for an all accepting FastAdapter
 */
typealias GenericFastAdapter = FastAdapter<GenericItem>

/**
 * The `FastAdapter` class is the core managing class of the `FastAdapter` library, it handles all `IAdapter` implementations, keeps track of the item types which can be displayed
 * and correctly provides the size and position and identifier information to the [RecyclerView].
 *
 *
 * It also comes with [IAdapterExtension] allowing to further modify its behaviour.
 * Additionally it allows to attach various different listener's, and also [EventHook]s on per item and view basis.
 *
 *
 * See the sample application for more details
 *
 * @param Item Defines the type of items this `FastAdapter` manages (in case of multiple different types, use `IItem`)
 */
@FastAdapterDsl
open class FastAdapter<Item : GenericItem> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // we remember all adapters
    //priority queue...
    private val adapters = ArrayList<IAdapter<Item>>()

    /**
     * Sets an item factory cache to this fast adapter instance.
     * The cache will manage the item factories to create new views more efficient.
     * Normally a shared cache is used over all adapter instances.
     *
     * DefaultItemFactoryCache a custom `IItemFactoryCache` implementation
     */
    open var itemVHFactoryCache: IItemVHFactoryCache<IItemVHFactory<*>> = DefaultItemVHFactoryCache()
    // cache the sizes of the different adapters so we can access the items more performant
    private val adapterSizes = SparseArray<IAdapter<Item>>()
    // the total size
    private var globalSize = 0

    private var _eventHooks: MutableList<EventHook<out Item>>? = null

    /** The eventHooks handled by this FastAdapter */
    val eventHooks: MutableList<EventHook<out Item>>
        get() = _eventHooks ?: LinkedList<EventHook<out Item>>().also { _eventHooks = it }

    // the extensions we support
    private val extensionsCache = ArrayMap<Class<*>, IAdapterExtension<Item>>()

    //
    //-------------------------
    //-------------------------
    //Selection stuff
    //-------------------------
    //-------------------------

    // legacy bindView mode. if activated we will forward onBindView without payloads to the method with payloads
    var legacyBindViewMode = false
    // if set to `false` will not attach any listeners to the list. click events will have to be handled manually
    var attachDefaultListeners = true

    private val logger = VerboseLogger(TAG)

    /** Enables the verbose log for the adapter */
    var verboseLoggingEnabled: Boolean
        get() = logger.isEnabled
        set(value) {
            logger.isEnabled = value
        }

    // the listeners which can be hooked on an item
    var onPreClickListener: ClickListener<Item>? = null
    var onClickListener: ClickListener<Item>? = null
    var onPreLongClickListener: LongClickListener<Item>? = null
    var onLongClickListener: LongClickListener<Item>? = null
    var onTouchListener: TouchListener<Item>? = null

    //the listeners for onCreateViewHolder or onBindViewHolder
    var onCreateViewHolderListener: OnCreateViewHolderListener<Item> = OnCreateViewHolderListenerImpl()
    var onBindViewHolderListener: OnBindViewHolderListener = OnBindViewHolderListenerImpl<Item>()

    /**
     * @return the AdapterExtensions we provided
     */
    val extensions: Collection<IAdapterExtension<Item>>
        get() = extensionsCache.values

    /** The ClickEventHook to hook onto the itemView of a viewholder */
    @Suppress("UNCHECKED_CAST")
    open val viewClickListener: ClickEventHook<Item> = object : ClickEventHook<Item>() {
        override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item) {
            if (!item.isEnabled) return
            val adapter = fastAdapter.getAdapter(position) ?: return
            if ((item as? IClickable<Item>)?.onPreItemClickListener?.invoke(v, adapter, item, position) == true) return
            if (fastAdapter.onPreClickListener?.invoke(v, adapter, item, position) == true) return
            for (ext in fastAdapter.extensionsCache.values) {
                if (ext.onClick(v, position, fastAdapter, item)) return
            }
            if ((item as? IClickable<Item>)?.onItemClickListener?.invoke(v, adapter, item, position) == true) return
            if (fastAdapter.onClickListener?.invoke(v, adapter, item, position) == true) return
        }
    }

    /** The LongClickEventHook to hook onto the itemView of a viewholder */
    open val viewLongClickListener: LongClickEventHook<Item> = object : LongClickEventHook<Item>() {
        override fun onLongClick(v: View, position: Int, fastAdapter: FastAdapter<Item>, item: Item): Boolean {
            if (!item.isEnabled) return false
            val adapter = fastAdapter.getAdapter(position) ?: return false
            if (fastAdapter.onPreLongClickListener?.invoke(v, adapter, item, position) == true) return true
            for (ext in fastAdapter.extensionsCache.values) {
                if (ext.onLongClick(v, position, fastAdapter, item)) return true
            }
            if (fastAdapter.onLongClickListener?.invoke(v, adapter, item, position) == true) return true
            return false
        }
    }

    /** The TouchEventHook to hook onto the itemView of a viewholder */
    open val viewTouchListener: TouchEventHook<Item> = object : TouchEventHook<Item>() {
        override fun onTouch(v: View, event: MotionEvent, position: Int, fastAdapter: FastAdapter<Item>, item: Item): Boolean {
            for (ext in fastAdapter.extensionsCache.values) {
                if (ext.onTouch(v, event, position, fastAdapter, item)) return true
            }
            if (fastAdapter.onTouchListener != null) {
                val adapter = fastAdapter.getAdapter(position)
                if (adapter != null) {
                    if (fastAdapter.onTouchListener?.invoke(v, event, adapter, item, position) == true) return true
                }
            }
            return false
        }
    }

    /**
     * Default CTOR
     */
    init {
        setHasStableIds(true)
    }

    /**
     * Adds a new adapter at the specific position
     *
     * @param index   the index where the new adapter should be added
     * @param adapter the new adapter to be added
     * @return this
     */
    open fun <A : IAdapter<Item>> addAdapter(index: Int, adapter: A): FastAdapter<Item> {
        adapters.add(index, adapter)
        prepareAdapters(adapter)
        return this
    }

    /**
     * Adds all new adapters at the end of the adapter list
     *
     * @param newAdapters the new adapters to be added
     * @return this
     */
    open fun <A : IAdapter<Item>> addAdapters(newAdapters: List<A>): FastAdapter<Item> {
        adapters.addAll(newAdapters as Collection<IAdapter<Item>>)
        newAdapters.forEach {
            prepareAdapters(it)
        }
        return this
    }

    /**
     * Prepares all adapters for their usage. update the fastAdapter, ensure all types are mapped, and update the order for the adapter.
     * It also updates the cached sizes.
     */
    private fun prepareAdapters(adapter: IAdapter<Item>) {
        adapter.fastAdapter = this
        adapters.forEachIndexed { i, item -> item.order = i }
        cacheSizes()
    }

    /**
     * Tries to get an adapter by a specific order
     *
     * @param order the order (position) to search the adapter at
     * @return the IAdapter if found
     */
    open fun adapter(order: Int): IAdapter<Item>? {
        return adapters.getOrNull(order)
    }

    fun <E : IAdapterExtension<Item>> addExtension(extension: E): FastAdapter<Item> {
        if (extensionsCache.containsKey(extension.javaClass)) {
            throw IllegalStateException("The given extension was already registered with this FastAdapter instance")
        }
        extensionsCache[extension.javaClass] = extension
        return this
    }

    /**
     * @param clazz the extension class, to retrieve its instance
     * @return the found IAdapterExtension or null if it is not found
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : IAdapterExtension<Item>> getExtension(clazz: Class<in T>): T? {
        return extensionsCache[clazz] as T?
    }

    inline fun <reified T : IAdapterExtension<Item>> getExtension(): T? = getExtension(T::class.java)

    inline fun <reified T : IAdapterExtension<Item>> requireExtension(): T = getExtension()!!

    @Suppress("UNCHECKED_CAST")
    fun <T : IAdapterExtension<Item>> getOrCreateExtension(clazz: Class<in T>): T? {
        if (extensionsCache.containsKey(clazz)) {
            return extensionsCache[clazz] as T
        }
        val extension = ExtensionsFactories.create(this, clazz as Class<out IAdapterExtension<out GenericItem>>) as? T
                ?: return null
        extensionsCache[clazz] = extension
        return extension
    }

    inline fun <reified T : IAdapterExtension<Item>> getOrCreateExtension(): T? = getOrCreateExtension(T::class.java)

    inline fun <reified T : IAdapterExtension<Item>> requireOrCreateExtension(): T = getOrCreateExtension(T::class.java)!!

    /**
     * Adds a new event hook for an item
     * NOTE: this has to be called before adding the first items, as this won't be called anymore after the ViewHolders were created
     *
     * @param eventHook the event hook to be added for an item
     * @return this
     */
    fun addEventHook(eventHook: EventHook<out Item>): FastAdapter<Item> {
        eventHooks.add(eventHook)
        return this
    }

    /**
     * Adds new event hooks for an item
     * NOTE: this has to be called before adding the first items, as this won't be called anymore after the ViewHolders were created
     *
     * @param eventHooks the event hooks to be added for an item
     * @return this
     */
    fun addEventHooks(eventHooks: Collection<EventHook<out Item>>): FastAdapter<Item> {
        this.eventHooks.addAll(eventHooks)
        return this
    }

    /**
     * Re-selects all elements stored in the savedInstanceState
     * IMPORTANT! Call this method only after all items where added to the adapters again. Otherwise it may select wrong items!
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in Note: Otherwise it is null.
     * @param prefix             a prefix added to the savedInstance key so we can store multiple states
     * @return this
     */
    @JvmOverloads
    fun withSavedInstanceState(savedInstanceState: Bundle?, prefix: String = ""): FastAdapter<Item> {
        for (ext in extensionsCache.values) {
            ext.withSavedInstanceState(savedInstanceState, prefix)
        }

        return this
    }

    /**
     * Register a new type into the TypeInstances to be able to efficiently create thew ViewHolders
     * @param item an IItem which will be shown in the list
     */
    @Deprecated("Register the factory instead", replaceWith = ReplaceWith("registerItemFactory(item)"))
    fun registerTypeInstance(item: Item) {
        if (item is IItemVHFactory<*>) {
            registerItemFactory(item.type, item)
        } else {
            item.factory?.let {
                registerItemFactory(item.type, it)
            }
        }
        // note if the item does not implement `IItemFactory<*>`, and thus requires you to register the factory for the type.
    }

    /**
     * Register a new type factory into the TypeInstances to be able to efficiently create thew ViewHolders
     *
     * @param item an IItem which will be shown in the list
     */
    fun registerItemFactory(type: Int, item: IItemVHFactory<*>) {
        itemVHFactoryCache.register(type, item)
    }

    /**
     * Gets the TypeInstance remembered within the FastAdapter for an item
     *
     * @param type the int type of the item
     * @return the Item typeInstance
     */
    fun getTypeInstance(type: Int): IItemVHFactory<*> {
        return itemVHFactoryCache[type]
    }

    /**
     * Clears the internal mapper - be sure, to remap everything before going on
     */
    fun clearTypeInstance() {
        itemVHFactoryCache.clear()
    }

    /**
     * Helper method to get the position from a holder
     * overwrite this if you have an adapter adding additional items inbetwean
     *
     * @param holder the viewHolder of the item
     * @return the position of the holder
     */
    open fun getHolderAdapterPosition(holder: RecyclerView.ViewHolder): Int {
        return holder.adapterPosition
    }

    /**
     * Creates the ViewHolder by the viewType
     *
     * @param parent   the parent view (the RecyclerView)
     * @param viewType the current viewType which is bound
     * @return the ViewHolder with the bound data
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        logger.log("onCreateViewHolder: $viewType")

        val itemFactory = getTypeInstance(viewType)
        val holder = onCreateViewHolderListener.onPreCreateViewHolder(this, parent, viewType, itemFactory)

        //set the adapter
        holder.itemView.setTag(R.id.fastadapter_item_adapter, this@FastAdapter)

        if (attachDefaultListeners) {
            //handle click behavior
            viewClickListener.attachToView(holder, holder.itemView)

            //handle long click behavior
            viewLongClickListener.attachToView(holder, holder.itemView)

            //handle touch behavior
            viewTouchListener.attachToView(holder, holder.itemView)
        }

        return onCreateViewHolderListener.onPostCreateViewHolder(this, holder, itemFactory)
    }

    /**
     * Binds the data to the created ViewHolder and sets the listeners to the holder.itemView
     * Note that you should use the `onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads`
     * as it allows you to implement a more efficient adapter implementation
     *
     * @param holder   the viewHolder we bind the data on
     * @param position the global position
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (legacyBindViewMode) {
            if (verboseLoggingEnabled) {
                Log.v(TAG, "onBindViewHolderLegacy: " + position + "/" + holder.itemViewType + " isLegacy: true")
            }
            //set the R.id.fastadapter_item_adapter tag to the adapter so we always have the proper bound adapter available
            holder.itemView.setTag(R.id.fastadapter_item_adapter, this)
            //now we bind the item to this viewHolder
            onBindViewHolderListener.onBindViewHolder(holder, position, Collections.emptyList())
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        //we do not want the binding to happen twice (the legacyBindViewMode
        if (!legacyBindViewMode) {
            if (verboseLoggingEnabled)
                Log.v(TAG, "onBindViewHolder: " + position + "/" + holder.itemViewType + " isLegacy: false")
            //set the R.id.fastadapter_item_adapter tag to the adapter so we always have the proper bound adapter available
            holder.itemView.setTag(R.id.fastadapter_item_adapter, this)
            //now we bind the item to this viewHolder
            onBindViewHolderListener.onBindViewHolder(holder, position, payloads)
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    /**
     * Unbinds the data to the already existing ViewHolder and removes the listeners from the holder.itemView
     *
     * @param holder the viewHolder we unbind the data from
     */
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        logger.log("onViewRecycled: " + holder.itemViewType)
        super.onViewRecycled(holder)
        onBindViewHolderListener.unBindViewHolder(holder, holder.adapterPosition)
    }

    /**
     * Is called in onViewDetachedFromWindow when the view is detached from the window
     *
     * @param holder the viewHolder for the view which got detached
     */
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        logger.log("onViewDetachedFromWindow: " + holder.itemViewType)
        super.onViewDetachedFromWindow(holder)
        onBindViewHolderListener.onViewDetachedFromWindow(holder, holder.adapterPosition)
    }

    /**
     * Is called in onViewAttachedToWindow when the view is detached from the window
     *
     * @param holder the viewHolder for the view which got detached
     */
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        logger.log("onViewAttachedToWindow: " + holder.itemViewType)
        super.onViewAttachedToWindow(holder)
        onBindViewHolderListener.onViewAttachedToWindow(holder, holder.adapterPosition)
    }

    /**
     * Is called when the ViewHolder is in a transient state. return true if you want to reuse
     * that view anyways
     *
     * @param holder the viewHolder for the view which failed to recycle
     * @return true if we want to recycle anyways (false - it get's destroyed)
     */
    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        logger.log("onFailedToRecycleView: " + holder.itemViewType)
        return onBindViewHolderListener.onFailedToRecycleView(holder, holder.adapterPosition) || super.onFailedToRecycleView(holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        logger.log("onAttachedToRecyclerView")
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        logger.log("onDetachedFromRecyclerView")
        super.onDetachedFromRecyclerView(recyclerView)
    }

    /**
     * Searches for the given item and calculates its global position
     *
     * @param item the item which is searched for
     * @return the global position, or [androidx.recyclerview.widget.RecyclerView.NO_POSITION] (-1) if not found
     */
    open fun getPosition(item: Item): Int {
        if (item.identifier == -1L) {
            Log.e(TAG, "You have to define an identifier for your item to retrieve the position via this method")
            return RecyclerView.NO_POSITION
        }
        return getPosition(item.identifier)
    }

    /**
     * Searches for the given item and calculates its global position
     *
     * @param identifier the identifier of an item which is searched for
     * @return the global position, or [androidx.recyclerview.widget.RecyclerView.NO_POSITION] (-1) if not found
     */
    open fun getPosition(identifier: Long): Int {
        var position = 0
        for (adapter in adapters) {
            if (adapter.order < 0) {
                continue
            }

            val relativePosition = adapter.getAdapterPosition(identifier)
            if (relativePosition != RecyclerView.NO_POSITION) {
                return position + relativePosition
            }
            position = adapter.adapterItemCount
        }

        return RecyclerView.NO_POSITION
    }

    /**
     * Gets the IItem by a position, from all registered adapters
     *
     * @param position the global position
     * @return the found IItem or null
     */
    open fun getItem(position: Int): Item? {
        //if we are out of range just return null
        if (position < 0 || position >= globalSize) {
            return null
        }
        //now get the adapter which is responsible for the given position
        val index = floorIndex(adapterSizes, position)
        return adapterSizes.valueAt(index).getAdapterItem(position - adapterSizes.keyAt(index))
    }

    /**
     * Gets the IItem given an identifier, from all registered adapters
     *
     * @param identifier the identifier of the searched item
     * @return the found Pair<IItem, Integer> (the found item, and it's global position if it is currently displayed) or null
     */
    open fun getItemById(identifier: Long): Pair<Item, Int?>? {
        if (identifier == -1L) {
            return null
        }
        val (_, second, third) = recursive(object : AdapterPredicate<Item> {
            override fun apply(
                    lastParentAdapter: IAdapter<Item>,
                    lastParentPosition: Int,
                    item: Item,
                    position: Int
            ): Boolean {
                return item.identifier == identifier
            }
        }, true)
        return second?.let { it to third }
    }

    /**
     * Internal method to get the Item as ItemHolder which comes with the relative position within its adapter
     * Finds the responsible adapter for the given position
     *
     * @param position the global position
     * @return the adapter which is responsible for this position
     */
    open fun getRelativeInfo(position: Int): RelativeInfo<Item> {
        if (position < 0 || position >= itemCount) {
            return RelativeInfo()
        }

        val relativeInfo = RelativeInfo<Item>()
        val index = floorIndex(adapterSizes, position)
        if (index != -1) {
            val peeked = adapterSizes.valueAt(index).peekAdapterItem(position - adapterSizes.keyAt(index))
            if (peeked != null) {
                relativeInfo.item = peeked
                relativeInfo.adapter = adapterSizes.valueAt(index)
                relativeInfo.position = position
            }
        }
        return relativeInfo
    }

    /**
     * Gets the adapter for the given position
     *
     * @param position the global position
     * @return the adapter responsible for this global position
     */
    open fun getAdapter(position: Int): IAdapter<Item>? {
        //if we are out of range just return null
        if (position < 0 || position >= globalSize) {
            return null
        }
        logger.log("getAdapter")
        //now get the adapter which is responsible for the given position
        return adapterSizes.valueAt(floorIndex(adapterSizes, position))
    }

    /**
     * Finds the int ItemViewType from the IItem which exists at the given position
     *
     * @param position the global position
     * @return the viewType for this position
     */
    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let {
            if (!itemVHFactoryCache.contains(it.type)) {
                registerTypeInstance(it)
            }
            it.type
        } ?: super.getItemViewType(position)
    }

    /**
     * Finds the int ItemId from the IItem which exists at the given position
     *
     * @param position the global position
     * @return the itemId for this position
     */
    override fun getItemId(position: Int): Long {
        return getItem(position)?.identifier ?: super.getItemId(position)
    }

    /**
     * Calculates the total ItemCount over all registered adapters
     *
     * @return the global count
     */
    override fun getItemCount(): Int {
        return globalSize
    }

    /**
     * Calculates the item count up to a given (excluding this) order number
     *
     * @param order the number up to which the items are counted
     * @return the total count of items up to the adapter order
     */
    open fun getPreItemCountByOrder(order: Int): Int {
        //if we are empty just return 0 count
        if (globalSize == 0) {
            return 0
        }

        var size = 0

        //count the number of items before the adapter with the given order
        for (i in 0 until min(order, adapters.size)) {
            size += adapters[i].adapterItemCount
        }

        //get the count of items which are before this order
        return size
    }


    /**
     * Calculates the item count up to a given (excluding this) adapter (defined by the global position of the item)
     *
     * @param position the global position of an adapter item
     * @return the total count of items up to the adapter which holds the given position
     */
    open fun getPreItemCount(position: Int): Int {
        //if we are empty just return 0 count
        return if (globalSize == 0) {
            0
        } else {
            adapterSizes.keyAt(floorIndex(adapterSizes, position))
        }
        //get the count of items which are before this order
    }

    /**
     * Add the values to the bundle for saveInstanceState
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in Note: Otherwise it is null.
     * @param prefix             a prefix added to the savedInstance key so we can store multiple states
     * @return the passed bundle with the newly added data
     */
    @JvmOverloads
    open fun saveInstanceState(savedInstanceState: Bundle, prefix: String = ""): Bundle {
        // handle our extensions
        for (ext in extensionsCache.values) {
            ext.saveInstanceState(savedInstanceState, prefix)
        }
        return savedInstanceState
    }

    /** We cache the sizes of our adapters so get accesses are faster */
    protected fun cacheSizes() {
        adapterSizes.clear()
        var size = 0

        for (adapter in adapters) {
            if (adapter.adapterItemCount > 0) {
                adapterSizes.append(size, adapter)
                size += adapter.adapterItemCount
            }
        }

        //we also have to add this for the first adapter otherwise the floorIndex method will return the wrong value
        if (size == 0 && adapters.size > 0) {
            adapterSizes.append(0, adapters[0])
        }

        globalSize = size
    }

    //-------------------------
    //-------------------------
    //wrap the notify* methods so we can have our required selection adjustment code
    //-------------------------
    //-------------------------

    /** Wraps notifyDataSetChanged */
    open fun notifyAdapterDataSetChanged() {
        // handle our extensions
        for (ext in extensionsCache.values) {
            ext.notifyAdapterDataSetChanged()
        }
        cacheSizes()
        notifyDataSetChanged()
    }

    /**
     * Wraps notifyItemInserted
     *
     * @param position the global position
     */
    open fun notifyAdapterItemInserted(position: Int) {
        notifyAdapterItemRangeInserted(position, 1)
    }

    /**
     * Wraps notifyItemRangeInserted
     *
     * @param position  the global position
     * @param itemCount the count of items inserted
     */
    open fun notifyAdapterItemRangeInserted(position: Int, itemCount: Int) {
        // handle our extensions
        for (ext in extensionsCache.values) {
            ext.notifyAdapterItemRangeInserted(position, itemCount)
        }
        cacheSizes()
        notifyItemRangeInserted(position, itemCount)
    }

    /**
     * wraps notifyItemRemoved
     *
     * @param position the global position
     */
    open fun notifyAdapterItemRemoved(position: Int) {
        notifyAdapterItemRangeRemoved(position, 1)
    }

    /**
     * Wraps notifyItemRangeRemoved
     *
     * @param position  the global position
     * @param itemCount the count of items removed
     */
    open fun notifyAdapterItemRangeRemoved(position: Int, itemCount: Int) {
        // handle our extensions
        for (ext in extensionsCache.values) {
            ext.notifyAdapterItemRangeRemoved(position, itemCount)
        }

        cacheSizes()
        notifyItemRangeRemoved(position, itemCount)
    }

    /**
     * Wraps notifyItemMoved
     *
     * @param fromPosition the global fromPosition
     * @param toPosition   the global toPosition
     */
    open fun notifyAdapterItemMoved(fromPosition: Int, toPosition: Int) {
        // handle our extensions
        for (ext in extensionsCache.values) {
            ext.notifyAdapterItemMoved(fromPosition, toPosition)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    /**
     * Wraps notifyItemChanged
     *
     * @param position the global position
     * @param payload  additional payload
     */
    @JvmOverloads
    open fun notifyAdapterItemChanged(position: Int, payload: Any? = null) {
        notifyAdapterItemRangeChanged(position, 1, payload)
    }

    /**
     * Wraps notifyItemRangeChanged
     *
     * @param position  the global position
     * @param itemCount the count of items changed
     * @param payload   an additional payload
     */
    @JvmOverloads
    open fun notifyAdapterItemRangeChanged(position: Int, itemCount: Int, payload: Any? = null) {
        // handle our extensions
        for (ext in extensionsCache.values) {
            ext.notifyAdapterItemRangeChanged(position, itemCount, payload)
        }
        if (payload == null) {
            notifyItemRangeChanged(position, itemCount)
        } else {
            notifyItemRangeChanged(position, itemCount, payload)
        }
    }

    /**
     * Util function which recursively iterates over all items and subItems of the given adapter.
     * It executes the given `predicate` on every item and will either stop if that function returns true, or continue (if stopOnMatch is false)
     *
     * @param predicate   the predicate to run on every item, to check for a match or do some changes (e.g. select)
     * @param stopOnMatch defines if we should stop iterating after the first match
     * @return Triple<Boolean, IItem, Integer> The first value is true (it is always not null), the second contains the item and the third the position (if the item is visible) if we had a match, (always false and null and null in case of stopOnMatch == false)
     */
    fun recursive(predicate: AdapterPredicate<Item>, stopOnMatch: Boolean): Triple<Boolean, Item, Int> {
        return recursive(predicate, 0, stopOnMatch)
    }

    /**
     * Util function which recursively iterates over all items and subItems of the given adapter.
     * It executes the given `predicate` on every item and will either stop if that function returns true, or continue (if stopOnMatch is false)
     *
     * @param predicate           the predicate to run on every item, to check for a match or do some changes (e.g. select)
     * @param globalStartPosition the start position at which we star tto recursively iterate over the items. (This will not stop at the end of a sub hierarchy!)
     * @param stopOnMatch         defines if we should stop iterating after the first match
     * @return Triple<Boolean, IItem, Integer> The first value is true (it is always not null), the second contains the item and the third the position (if the item is visible) if we had a match, (always false and null and null in case of stopOnMatch == false)
     */
    fun recursive(predicate: AdapterPredicate<Item>, globalStartPosition: Int, stopOnMatch: Boolean): Triple<Boolean, Item, Int> {
        for (i in globalStartPosition until itemCount) {
            //retrieve the item + it's adapter
            val relativeInfo = getRelativeInfo(i)
            val item = relativeInfo.item
            if (item != null) {
                relativeInfo.adapter?.let { adapter ->
                    if (predicate.apply(adapter, i, item, i) && stopOnMatch) {
                        return Triple(true, item, i)
                    }
                    (item as? IExpandable<*>)?.let { expandableItem ->
                        val res = recursiveSub(adapter, i, expandableItem, predicate, stopOnMatch)
                        if (res.first && stopOnMatch) {
                            return res
                        }
                    }
                }
            }
        }

        return Triple(false, null, null)
    }

    /**
     * An internal class to return the IItem and relativePosition and its adapter at once. used to save one iteration inside the getInternalItem method
     */
    class RelativeInfo<Item : GenericItem> {
        var adapter: IAdapter<Item>? = null
        var item: Item? = null
        var position = RecyclerView.NO_POSITION
    }

    /**
     * A ViewHolder provided from the FastAdapter to allow handling the important event's within the ViewHolder
     * instead of the item
     *
     * @param Item
     */
    abstract class ViewHolder<Item : GenericItem>(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /** Binds the data of this item onto the viewHolder */
        abstract fun bindView(item: Item, payloads: List<Any>)

        /** View needs to release resources when its recycled */
        abstract fun unbindView(item: Item)

        /** View got attached to the window */
        open fun attachToWindow(item: Item) {}

        /** View got detached from the window */
        open fun detachFromWindow(item: Item) {}

        /**
         * View is in a transient state and could not be recycled
         *
         * @return return true if you want to recycle anyways (after clearing animations or so)
         */
        open fun failedToRecycle(item: Item): Boolean {
            return false
        }
    }

    companion object {
        private const val TAG = "FastAdapter"

        private fun floorIndex(sparseArray: SparseArray<*>, key: Int): Int {
            var index = sparseArray.indexOfKey(key)
            if (index < 0) {
                index = index.inv() - 1
            }
            return index
        }

        /**
         * Creates a new FastAdapter with the provided adapters
         * if adapters is null, a default ItemAdapter is defined
         *
         * @param adapter the adapters which this FastAdapter should use
         * @return a new FastAdapter
         */
        @JvmStatic
        fun <Item : GenericItem> with(adapter: IAdapter<Item>): FastAdapter<Item> {
            val fastAdapter = FastAdapter<Item>()
            fastAdapter.addAdapter(0, adapter)
            return fastAdapter
        }

        /**
         * Creates a new FastAdapter with the provided adapters
         * if adapters is null, a default ItemAdapter is defined
         *
         * @param adapters the adapters which this FastAdapter should use
         * @return a new FastAdapter
         */
        @JvmStatic
        fun <Item : GenericItem> with(adapters: Collection<IAdapter<out Item>>?): FastAdapter<Item> {
            return with(adapters, null)
        }

        /**
         * Creates a new FastAdapter with the provided adapters
         * if adapters is null, a default ItemAdapter is defined
         *
         * @param adapters the adapters which this FastAdapter should use
         * @return a new FastAdapter
         */
        @JvmStatic
        fun <Item : GenericItem> with(adapters: Collection<IAdapter<out Item>>?, extensions: Collection<IAdapterExtension<Item>>? = null): FastAdapter<Item> {
            val fastAdapter = FastAdapter<Item>()
            if (adapters == null) {
                fastAdapter.adapters.add(items<GenericItem>() as IAdapter<Item>)
            } else {
                fastAdapter.adapters.addAll(adapters as Collection<IAdapter<Item>>)
            }
            for (i in fastAdapter.adapters.indices) {
                fastAdapter.adapters[i].apply {
                    this.fastAdapter = fastAdapter
                    this.order = i
                }
            }
            fastAdapter.cacheSizes()

            extensions?.forEach {
                fastAdapter.addExtension(it)
            }

            return fastAdapter
        }

        @JvmStatic
        fun <Item : GenericItem> getFromHolderTag(holder: RecyclerView.ViewHolder?): FastAdapter<Item>? =
                holder?.itemView?.getTag(R.id.fastadapter_item_adapter) as? FastAdapter<Item>

        /**
         * Convenient helper method to get the Item from a holder
         *
         * @param holder the ViewHolder for which we want to retrieve the item
         * @return the Item found for this ViewHolder
         */
        @JvmStatic
        fun <Item : GenericItem> getHolderAdapterItem(holder: RecyclerView.ViewHolder?): Item? {
            holder ?: return null
            val adapter = getFromHolderTag<Item>(holder) ?: return null
            val pos = adapter.getHolderAdapterPosition(holder).takeIf { it != RecyclerView.NO_POSITION }
                    ?: return null
            return adapter.getItem(pos)
        }

        /**
         * Convenient helper method to get the Item from a holder
         *
         * @param holder   the ViewHolder for which we want to retrieve the item
         * @param position the position for which we want to retrieve the item
         * @return the Item found for the given position and that ViewHolder
         */
        @JvmStatic
        fun <Item : GenericItem> getHolderAdapterItem(holder: RecyclerView.ViewHolder?, position: Int): Item? =
                getFromHolderTag<Item>(holder)?.getItem(position)

        /**
         * Convenient helper method to get the Item from a holder via the defined tag
         *
         * @param holder the ViewHolder for which we want to retrieve the item
         * @return the Item found for the given position and that ViewHolder
         */
        @JvmStatic
        fun <Item : GenericItem> getHolderAdapterItemTag(holder: RecyclerView.ViewHolder?): Item? =
                holder?.itemView?.getTag(R.id.fastadapter_item) as? Item

        /**
         * Util function which recursively iterates over all items of a `IExpandable` parent if and only if it is `expanded` and has `subItems`
         * This is usually only used in
         *
         * @param lastParentAdapter  the last `IAdapter` managing the last (visible) parent item (that might also be a parent of a parent, ..)
         * @param lastParentPosition the global position of the last (visible) parent item, holding this sub item (that might also be a parent of a parent, ..)
         * @param parent             the `IExpandableParent` to start from
         * @param predicate          the predicate to run on every item, to check for a match or do some changes (e.g. select)
         * @param stopOnMatch        defines if we should stop iterating after the first match
         * @param Item               the type of the `Item`
         * @return The first value is true (it is always not null), the second contains the item and the third the position (if the item is visible) if we had a match, (always false and null and null in case of stopOnMatch == false)
         */
        @JvmStatic
        fun <Item : GenericItem> recursiveSub(
                lastParentAdapter: IAdapter<Item>,
                lastParentPosition: Int,
                parent: IExpandable<*>,
                predicate: AdapterPredicate<Item>,
                stopOnMatch: Boolean
        ): Triple<Boolean, Item, Int> {
            //in case it's expanded it can be selected via the normal way
            if (!parent.isExpanded) {
                parent.subItems.forEach { sub ->
                    (sub as Item).let { subItem ->
                        if (predicate.apply(lastParentAdapter, lastParentPosition, subItem, RecyclerView.NO_POSITION) && stopOnMatch) {
                            return Triple(true, sub, null)
                        }
                    }
                    if (sub is IExpandable<*>) {
                        val res = recursiveSub(lastParentAdapter, lastParentPosition, sub, predicate, stopOnMatch)
                        if (res.first) {
                            return res
                        }
                    }
                }
            }
            return Triple(false, null, null)
        }
    }
}
