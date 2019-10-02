package com.mikepenz.fastadapter

import android.os.Bundle
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.listeners.*
import com.mikepenz.fastadapter.utils.AdapterPredicate
import com.mikepenz.fastadapter.utils.Triple
import java.util.*

/**
 * todo
 */
interface IFastAdapter<Item : GenericItem> {
    // we remember all adapters
    //priority queue...
    val adapters: ArrayList<IAdapter<Item>>

    /**
     * Sets an type instance cache to this fast adapter instance.
     * The cache will manage the type instances to create new views more efficient.
     * Normally an shared cache is used over all adapter instances.
     *
     * typeInstanceCache a custom `TypeInstanceCache` implementation
     */
    var typeInstanceCache: ITypeInstanceCache<Item>
    // cache the sizes of the different adapters so we can access the items more performant
    val adapterSizes: SparseArray<IAdapter<Item>>
    // the total size
    var globalSize: Int

    /**
     * The eventHooks handled by this FastAdapter
     */
    val eventHooks: MutableList<EventHook<out Item>>

    // the extensions we support
    val extensionsCache: ArrayMap<Class<*>, IAdapterExtension<Item>>

    //
    //-------------------------
    //-------------------------
    //Selection stuff
    //-------------------------
    //-------------------------

    // legacy bindView mode. if activated we will forward onBindView without payloads to the method with payloads
    var legacyBindViewMode: Boolean
    // if set to `false` will not attach any listeners to the list. click events will have to be handled manually
    var attachDefaultListeners: Boolean

    /**
     * enables the verbose log for the adapter
     **/
    var verboseLoggingEnabled: Boolean

    // the listeners which can be hooked on an item
    var onPreClickListener: ClickListener<Item>?
    var onClickListener: ClickListener<Item>?
    var onPreLongClickListener: ((v: View, adapter: IAdapter<Item>, item: Item, position: Int) -> Boolean)?
    var onLongClickListener: ((v: View, adapter: IAdapter<Item>, item: Item, position: Int) -> Boolean)?
    var onTouchListener: ((v: View, event: MotionEvent, adapter: IAdapter<Item>, item: Item, position: Int) -> Boolean)?

    //the listeners for onCreateViewHolder or onBindViewHolder
    var onCreateViewHolderListener: OnCreateViewHolderListener<Item>
    var onBindViewHolderListener: OnBindViewHolderListener

    /**
     * @return the AdapterExtensions we provided
     */
    val extensions: Collection<IAdapterExtension<Item>>

    /**
     * the ClickEventHook to hook onto the itemView of a viewholder
     */
    val viewClickListener: ClickEventHook<Item>

    /**
     * the LongClickEventHook to hook onto the itemView of a viewholder
     */
    val viewLongClickListener: LongClickEventHook<Item>

    /**
     * the TouchEventHook to hook onto the itemView of a viewholder
     */
    val viewTouchListener: TouchEventHook<Item>

    /**
     * Returns the adapter casted to a RecyclerView.Adapter
     */
    fun asRecyclerViewAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>

    /**
     * add's a new adapter at the specific position
     *
     * @param index   the index where the new adapter should be added
     * @param adapter the new adapter to be added
     * @return this
     */
    fun <A : IAdapter<Item>> addAdapter(index: Int, adapter: A): IFastAdapter<Item>

    /**
     * adds all new adapters at the end of the adapter list
     *
     * @param newAdapters the new adapters to be added
     * @return this
     */
    fun <A : IAdapter<Item>> addAdapters(newAdapters: List<A>): IFastAdapter<Item>

    /**
     * Tries to get an adapter by a specific order
     *
     * @param order the order (position) to search the adapter at
     * @return the IAdapter if found
     */
    fun adapter(order: Int): IAdapter<Item>?

    /**
     * @param extension
     * @return
     */
    fun <E : IAdapterExtension<Item>> addExtension(extension: E): IFastAdapter<Item>

    /**
     * @param clazz the extension class, to retrieve its instance
     * @return the found IAdapterExtension or null if it is not found
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : IAdapterExtension<Item>> getExtension(clazz: Class<in T>): T?

    @Suppress("UNCHECKED_CAST")
    fun <T : IAdapterExtension<Item>> getOrCreateExtension(clazz: Class<in T>): T?

    /**
     * adds a new event hook for an item
     * NOTE: this has to be called before adding the first items, as this won't be called anymore after the ViewHolders were created
     *
     * @param eventHook the event hook to be added for an item
     * @return this
     */
    fun addEventHook(eventHook: EventHook<out Item>): IFastAdapter<Item>

    /**
     * adds new event hooks for an item
     * NOTE: this has to be called before adding the first items, as this won't be called anymore after the ViewHolders were created
     *
     * @param eventHooks the event hooks to be added for an item
     * @return this
     */
    fun addEventHooks(eventHooks: Collection<EventHook<out Item>>): IFastAdapter<Item>

    /**
     * re-selects all elements stored in the savedInstanceState
     * IMPORTANT! Call this method only after all items where added to the adapters again. Otherwise it may select wrong items!
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in Note: Otherwise it is null.
     * @param prefix             a prefix added to the savedInstance key so we can store multiple states
     * @return this
     */
    fun withSavedInstanceState(savedInstanceState: Bundle?, prefix: String = ""): IFastAdapter<Item>

    /**
     * register a new type into the TypeInstances to be able to efficiently create thew ViewHolders
     *
     * @param item an IItem which will be shown in the list
     */
    fun registerTypeInstance(item: Item)

    /**
     * gets the TypeInstance remembered within the FastAdapter for an item
     *
     * @param type the int type of the item
     * @return the Item typeInstance
     */
    fun getTypeInstance(type: Int): Item

    /**
     * clears the internal mapper - be sure, to remap everything before going on
     */
    fun clearTypeInstance()

    /**
     * helper method to get the position from a holder
     * overwrite this if you have an adapter adding additional items inbetwean
     *
     * @param holder the viewHolder of the item
     * @return the position of the holder
     */
    fun getHolderAdapterPosition(holder: RecyclerView.ViewHolder): Int

    /**
     * Creates the ViewHolder by the viewType
     *
     * @param parent   the parent view (the RecyclerView)
     * @param viewType the current viewType which is bound
     * @return the ViewHolder with the bound data
     */
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    /**
     * Binds the data to the created ViewHolder and sets the listeners to the holder.itemView
     * Note that you should use the `onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads`
     * as it allows you to implement a more efficient adapter implementation
     *
     * @param holder   the viewHolder we bind the data on
     * @param position the global position
     */
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>)

    /**
     * Unbinds the data to the already existing ViewHolder and removes the listeners from the holder.itemView
     *
     * @param holder the viewHolder we unbind the data from
     */
    fun onViewRecycled(holder: RecyclerView.ViewHolder)

    /**
     * is called in onViewDetachedFromWindow when the view is detached from the window
     *
     * @param holder the viewHolder for the view which got detached
     */
    fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder)

    /**
     * is called in onViewAttachedToWindow when the view is detached from the window
     *
     * @param holder the viewHolder for the view which got detached
     */
    fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder)

    /**
     * is called when the ViewHolder is in a transient state. return true if you want to reuse
     * that view anyways
     *
     * @param holder the viewHolder for the view which failed to recycle
     * @return true if we want to recycle anyways (false - it get's destroyed)
     */
    fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean

    fun onAttachedToRecyclerView(recyclerView: RecyclerView)

    fun onDetachedFromRecyclerView(recyclerView: RecyclerView)

    /**
     * Searches for the given item and calculates its global position
     *
     * @param item the item which is searched for
     * @return the global position, or -1 if not found
     */
    fun getPosition(item: Item): Int

    /**
     * Searches for the given item and calculates its global position
     *
     * @param identifier the identifier of an item which is searched for
     * @return the global position, or -1 if not found
     */
    fun getPosition(identifier: Long): Int

    /**
     * gets the IItem by a position, from all registered adapters
     *
     * @param position the global position
     * @return the found IItem or null
     */
    fun getItem(position: Int): Item?

    /**
     * gets the IItem given an identifier, from all registered adapters
     *
     * @param identifier the identifier of the searched item
     * @return the found Pair&lt;IItem, Integer&gt; (the found item, and it's global position if it is currently displayed) or null
     */
    fun getItemById(identifier: Long): Pair<Item, Int?>?

    /**
     * Internal method to get the Item as ItemHolder which comes with the relative position within its adapter
     * Finds the responsible adapter for the given position
     *
     * @param position the global position
     * @return the adapter which is responsible for this position
     */
    fun getRelativeInfo(position: Int): RelativeInfo<Item>

    /**
     * Gets the adapter for the given position
     *
     * @param position the global position
     * @return the adapter responsible for this global position
     */
    fun getAdapter(position: Int): IAdapter<Item>?

    /**
     * finds the int ItemViewType from the IItem which exists at the given position
     *
     * @param position the global position
     * @return the viewType for this position
     */
    fun getItemViewType(position: Int): Int

    /**
     * finds the int ItemId from the IItem which exists at the given position
     *
     * @param position the global position
     * @return the itemId for this position
     */
    fun getItemId(position: Int): Long

    /**
     * calculates the total ItemCount over all registered adapters
     *
     * @return the global count
     */
    fun getItemCount(): Int

    /**
     * calculates the item count up to a given (excluding this) order number
     *
     * @param order the number up to which the items are counted
     * @return the total count of items up to the adapter order
     */
    fun getPreItemCountByOrder(order: Int): Int

    /**
     * calculates the item count up to a given (excluding this) adapter (defined by the global position of the item)
     *
     * @param position the global position of an adapter item
     * @return the total count of items up to the adapter which holds the given position
     */
    fun getPreItemCount(position: Int): Int

    /**
     * add the values to the bundle for saveInstanceState
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in Note: Otherwise it is null.
     * @param prefix             a prefix added to the savedInstance key so we can store multiple states
     * @return the passed bundle with the newly added data
     */
    fun saveInstanceState(savedInstanceState: Bundle, prefix: String = ""): Bundle

    //-------------------------
    //-------------------------
    //wrap the notify* methods so we can have our required selection adjustment code
    //-------------------------
    //-------------------------

    /**
     * wraps notifyDataSetChanged
     */
    fun notifyAdapterDataSetChanged()

    /**
     * wraps notifyItemInserted
     *
     * @param position the global position
     */
    fun notifyAdapterItemInserted(position: Int)

    /**
     * wraps notifyItemRangeInserted
     *
     * @param position  the global position
     * @param itemCount the count of items inserted
     */
    fun notifyAdapterItemRangeInserted(position: Int, itemCount: Int)

    /**
     * wraps notifyItemRemoved
     *
     * @param position the global position
     */
    fun notifyAdapterItemRemoved(position: Int)

    /**
     * wraps notifyItemRangeRemoved
     *
     * @param position  the global position
     * @param itemCount the count of items removed
     */
    fun notifyAdapterItemRangeRemoved(position: Int, itemCount: Int)

    /**
     * wraps notifyItemMoved
     *
     * @param fromPosition the global fromPosition
     * @param toPosition   the global toPosition
     */
    fun notifyAdapterItemMoved(fromPosition: Int, toPosition: Int)

    /**
     * wraps notifyItemChanged
     *
     * @param position the global position
     * @param payload  additional payload
     */
    fun notifyAdapterItemChanged(position: Int, payload: Any? = null)

    /**
     * wraps notifyItemRangeChanged
     *
     * @param position  the global position
     * @param itemCount the count of items changed
     * @param payload   an additional payload
     */
    fun notifyAdapterItemRangeChanged(position: Int, itemCount: Int, payload: Any? = null)

    /**
     * util function which recursively iterates over all items and subItems of the given adapter.
     * It executes the given `predicate` on every item and will either stop if that function returns true, or continue (if stopOnMatch is false)
     *
     * @param predicate   the predicate to run on every item, to check for a match or do some changes (e.g. select)
     * @param stopOnMatch defines if we should stop iterating after the first match
     * @return Triple&lt;Boolean, IItem, Integer&gt; The first value is true (it is always not null), the second contains the item and the third the position (if the item is visible) if we had a match, (always false and null and null in case of stopOnMatch == false)
     */
    fun recursive(predicate: AdapterPredicate<Item>, stopOnMatch: Boolean): Triple<Boolean, Item, Int>

    /**
     * util function which recursively iterates over all items and subItems of the given adapter.
     * It executes the given `predicate` on every item and will either stop if that function returns true, or continue (if stopOnMatch is false)
     *
     * @param predicate           the predicate to run on every item, to check for a match or do some changes (e.g. select)
     * @param globalStartPosition the start position at which we star tto recursively iterate over the items. (This will not stop at the end of a sub hierarchy!)
     * @param stopOnMatch         defines if we should stop iterating after the first match
     * @return Triple&lt;Boolean, IItem, Integer&gt; The first value is true (it is always not null), the second contains the item and the third the position (if the item is visible) if we had a match, (always false and null and null in case of stopOnMatch == false)
     */
    fun recursive(predicate: AdapterPredicate<Item>, globalStartPosition: Int, stopOnMatch: Boolean): Triple<Boolean, Item, Int>

    /**
     * A ViewHolder provided from the FastAdapter to allow handling the important event's within the ViewHolder
     * instead of the item
     *
     * @param <Item>
    </Item> */
    abstract class ViewHolder<Item : GenericItem>(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * binds the data of this item onto the viewHolder
         */
        abstract fun bindView(item: Item, payloads: MutableList<Any>)

        /**
         * View needs to release resources when its recycled
         */
        abstract fun unbindView(item: Item)

        /**
         * View got attached to the window
         */
        fun attachToWindow(item: Item) {}

        /**
         * View got detached from the window
         */
        fun detachFromWindow(item: Item) {}

        /**
         * View is in a transient state and could not be recycled
         *
         * @return return true if you want to recycle anyways (after clearing animations or so)
         */
        fun failedToRecycle(item: Item): Boolean {
            return false
        }
    }
}

/**
 * an internal class to return the IItem and relativePosition and its adapter at once. used to save one iteration inside the getInternalItem method
 */
open class RelativeInfo<Item : GenericItem> {
    var adapter: IAdapter<Item>? = null
    var item: Item? = null
    var position = -1
}