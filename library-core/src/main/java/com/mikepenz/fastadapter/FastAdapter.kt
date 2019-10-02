package com.mikepenz.fastadapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.dsl.FastAdapterDsl
import com.mikepenz.fastadapter.listeners.EventHook
import com.mikepenz.fastadapter.utils.AdapterPredicate
import com.mikepenz.fastadapter.utils.Triple

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
open class FastAdapter<Item : GenericItem>(
        val engine: FastAdapterEngine<Item> = FastAdapterEngine()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), IFastAdapter<Item> by engine, ISuperDelegate {

    init {
        @Suppress("LeakingThis")
        engine.rvAdapter = this
        @Suppress("LeakingThis")
        engine.superDelegate = this // allows to call the super methods from the [RecyclerView.Adapter] this is required as we would otherwise end in a endless recursion
        this.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return engine.getItemId(position)
    }

    override fun superGetItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return engine.getItemViewType(position)
    }

    override fun superGetItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        engine.onAttachedToRecyclerView(recyclerView)
    }

    override fun superOnAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        engine.onBindViewHolder(holder, position, payloads)
        }

    override fun superOnBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        engine.onDetachedFromRecyclerView(recyclerView)
    }

    override fun superOnDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return engine.onFailedToRecycleView(holder)
    }

    override fun superOnFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return super.onFailedToRecycleView(holder)
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        engine.onViewAttachedToWindow(holder)
    }

    override fun superOnViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        engine.onViewDetachedFromWindow(holder)
            }

    override fun superOnViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        engine.onViewRecycled(holder)
    }

    override fun superOnViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        }

    inline fun <reified T : IAdapterExtension<Item>> getExtension(): T? = getExtension(T::class.java)

    inline fun <reified T : IAdapterExtension<Item>> requireExtension(): T = getExtension()!!

    inline fun <reified T : IAdapterExtension<Item>> getOrCreateExtension(): T? = getOrCreateExtension(T::class.java)

    inline fun <reified T : IAdapterExtension<Item>> requireOrCreateExtension(): T = getOrCreateExtension(T::class.java)!!

    /**
     * an internal class to return the IItem and relativePosition and its adapter at once. used to save one iteration inside the getInternalItem method
     */
    @Deprecated(message = "Refactor to use the `RelativeInfo` from the `IFastAdapter` class", replaceWith = ReplaceWith("RelativeInfo", "com.mikepenz.fastadapter.RelativeInfo"))
    class RelativeInfo<Item : GenericItem> : com.mikepenz.fastadapter.RelativeInfo<Item>()

    /**
     * A ViewHolder provided from the FastAdapter to allow handling the important event's within the ViewHolder
     * instead of the item
     *
     * @param <Item>
    </Item> */
    @Deprecated(message = "Refactor to use the `ViewHolder` from the `IFastAdapter` class", replaceWith = ReplaceWith("ViewHolder", "com.mikepenz.fastadapter.IFastAdapter.ViewHolder"))
    abstract class ViewHolder<Item : GenericItem>(itemView: View) : IFastAdapter.ViewHolder<Item>(itemView)

    companion object {
        private const val TAG = "FastAdapter"

        /**
         * creates a new FastAdapter with the provided adapters
         * if adapters is null, a default ItemAdapter is defined
         *
         * @param adapter the adapters which this FastAdapter should use
         * @return a new FastAdapter
         */
        @JvmStatic
        fun <Item : GenericItem, A : IAdapter<Item>> with(adapter: A): FastAdapter<Item> {
            val fastAdapter = FastAdapter<Item>()
            fastAdapter.addAdapter(0, adapter)
            return fastAdapter
        }

        /**
         * creates a new FastAdapter with the provided adapters
         * if adapters is null, a default ItemAdapter is defined
         *
         * @param adapters the adapters which this FastAdapter should use
         * @return a new FastAdapter
         */
        @JvmStatic
        fun <Item : GenericItem, A : IAdapter<*>> with(adapters: Collection<A>?): FastAdapter<Item> {
            return with(adapters, null)
        }

        /**
         * creates a new FastAdapter with the provided adapters
         * if adapters is null, a default ItemAdapter is defined
         *
         * @param adapters the adapters which this FastAdapter should use
         * @return a new FastAdapter
         */
        @JvmStatic
        fun <Item : GenericItem, A : IAdapter<*>> with(adapters: Collection<A>?, extensions: Collection<IAdapterExtension<Item>>?): FastAdapter<Item> {
            val fastAdapter = FastAdapter<Item>()
            if (adapters == null) {
                fastAdapter.adapters.add(ItemAdapter.items<GenericItem>() as IAdapter<Item>)
            } else {
                fastAdapter.adapters.addAll(adapters as Collection<IAdapter<Item>>)
            }
            for (i in fastAdapter.adapters.indices) {
                fastAdapter.adapters[i].apply {
                    this.fastAdapter = fastAdapter
                    this.order = i
                }
            }
            fastAdapter.engine.cacheSizes()

            extensions?.forEach {
                fastAdapter.addExtension(it)
            }

            return fastAdapter
        }

        @JvmStatic
        fun <Item : GenericItem> getFromHolderTag(holder: RecyclerView.ViewHolder?): IFastAdapter<Item>? = FastAdapterEngine.getFromHolderTag(holder)

        /**
         * convenient helper method to get the Item from a holder
         *
         * @param holder the ViewHolder for which we want to retrieve the item
         * @return the Item found for this ViewHolder
         */
        @JvmStatic
        fun <Item : GenericItem> getHolderAdapterItem(holder: RecyclerView.ViewHolder?): Item? = FastAdapterEngine.getHolderAdapterItem(holder)

        /**
         * convenient helper method to get the Item from a holder
         *
         * @param holder   the ViewHolder for which we want to retrieve the item
         * @param position the position for which we want to retrieve the item
         * @return the Item found for the given position and that ViewHolder
         */
        @JvmStatic
        fun <Item : GenericItem> getHolderAdapterItem(holder: RecyclerView.ViewHolder?, position: Int): Item? = FastAdapterEngine.getHolderAdapterItem(holder, position)

        /**
         * convenient helper method to get the Item from a holder via the defined tag
         *
         * @param holder the ViewHolder for which we want to retrieve the item
         * @return the Item found for the given position and that ViewHolder
         */
        @JvmStatic
        fun <Item : GenericItem> getHolderAdapterItemTag(holder: RecyclerView.ViewHolder?): Item? = FastAdapterEngine.getHolderAdapterItemTag(holder)

        /**
         * Util function which recursively iterates over all items of a `IExpandable` parent if and only if it is `expanded` and has `subItems`
         * This is usually only used in
         *
         * @param lastParentAdapter  the last `IAdapter` managing the last (visible) parent item (that might also be a parent of a parent, ..)
         * @param lastParentPosition the global position of the last (visible) parent item, holding this sub item (that might also be a parent of a parent, ..)
         * @param parent             the `IExpandableParent` to start from
         * @param predicate          the predicate to run on every item, to check for a match or do some changes (e.g. select)
         * @param stopOnMatch        defines if we should stop iterating after the first match
         * @param <Item>             the type of the `Item`
         * @return Triple&lt;Boolean, IItem, Integer&gt; The first value is true (it is always not null), the second contains the item and the third the position (if the item is visible) if we had a match, (always false and null and null in case of stopOnMatch == false)
        </Item> */
        @JvmStatic
        fun <Item : GenericItem> recursiveSub(
                lastParentAdapter: IAdapter<Item>,
                lastParentPosition: Int,
                parent: IExpandable<*>,
                predicate: AdapterPredicate<Item>,
                stopOnMatch: Boolean
        ): Triple<Boolean, Item, Int> {
            return FastAdapterEngine.recursiveSub(lastParentAdapter, lastParentPosition, parent, predicate, stopOnMatch)
        }
    }
}
