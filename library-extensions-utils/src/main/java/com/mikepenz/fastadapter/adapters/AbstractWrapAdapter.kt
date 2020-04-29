package com.mikepenz.fastadapter.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.IItemVHFactory

/**
 * Created by mikepenz on 03.03.16.
 */
abstract class AbstractWrapAdapter<Item : IItem<VH>, VH : RecyclerView.ViewHolder>(open var items: List<Item>) : RecyclerView.Adapter<VH>() {
    //private AbstractAdapter mParentAdapter;
    //keep a reference to the FastAdapter which contains the base logic
    /**
     * @return the reference to the FastAdapter
     */
    var adapter: RecyclerView.Adapter<VH>? = null
        private set


    /**
     * Wrap the FastAdapter with this AbstractAdapter and keep its reference to forward all events correctly
     *
     * @param adapter the FastAdapter which contains the base logic
     * @return this
     */
    fun wrap(adapter: RecyclerView.Adapter<VH>): AbstractWrapAdapter<Item, VH> {
        //this.mParentAdapter = abstractAdapter;
        this.adapter = adapter
        return this
    }

    /** This method states if we should insert a custom element at the given position */
    abstract fun shouldInsertItemAtPosition(position: Int): Boolean

    /** This method calculates how many elements were already inserted before this position */
    abstract fun itemInsertedBeforeCount(position: Int): Int


    /** Overwrite the [RecyclerView.Adapter.registerAdapterDataObserver] to correctly forward all events to the FastAdapter */
    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
        adapter?.registerAdapterDataObserver(observer)
    }

    /** Overwrite the [RecyclerView.Adapter.unregisterAdapterDataObserver] to correctly forward all events to the FastAdapter */
    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.unregisterAdapterDataObserver(observer)
        adapter?.unregisterAdapterDataObserver(observer)
    }

    /** Overwrite the [RecyclerView.Adapter.getItemViewType] to correctly return the value from the FastAdapter */
    override fun getItemViewType(position: Int): Int {
        return if (shouldInsertItemAtPosition(position)) {
            getItem(position)?.type ?: 0
        } else {
            adapter?.getItemViewType(position - itemInsertedBeforeCount(position)) ?: 0
        }
    }

    /** Overwrite the [RecyclerView.Adapter.getItemId] to correctly return the value from the FastAdapter */
    override fun getItemId(position: Int): Long {
        return if (shouldInsertItemAtPosition(position)) {
            getItem(position)?.identifier ?: 0
        } else {
            adapter?.getItemId(position - itemInsertedBeforeCount(position)) ?: 0
        }
    }

    /** Make sure we return the Item from the FastAdapter so we retrieve the item from all adapters */
    fun getItem(position: Int): Item? {
        return if (shouldInsertItemAtPosition(position)) {
            items[itemInsertedBeforeCount(position - 1)]
        } else {
            null
        }
    }

    /** Make sure we return the count from the FastAdapter so we retrieve the count from all adapters */
    override fun getItemCount(): Int {
        val itemCount = adapter?.itemCount ?: 0
        return itemCount + itemInsertedBeforeCount(itemCount)
    }

    /** The [RecyclerView.Adapter.onCreateViewHolder] is managed by the FastAdapter so forward this correctly */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        //TODO OPTIMIZE
        val vh = items.asSequence().mapNotNull { it to it as? IItemVHFactory<VH> }.firstOrNull { (item, factory) -> item.type == viewType }?.second?.getViewHolder(parent)
        if (vh != null) {
            return vh
        }
        val adapter = this.adapter ?: throw RuntimeException("A adapter needs to be wrapped")
        return adapter.onCreateViewHolder(parent, viewType)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        //empty implementation as the one with the List payloads is already called
    }

    /** The [RecyclerView.Adapter.onBindViewHolder] is managed by the FastAdapter so forward this correctly */
    override fun onBindViewHolder(holder: VH, position: Int, payloads: List<Any>) {
        if (shouldInsertItemAtPosition(position)) {
            getItem(position)?.bindView(holder, payloads)
        } else {
            adapter?.onBindViewHolder(holder, position - itemInsertedBeforeCount(position), payloads)
        }
    }

    /** The [RecyclerView.Adapter.setHasStableIds] is managed by the FastAdapter so forward this correctly */
    override fun setHasStableIds(hasStableIds: Boolean) {
        adapter?.setHasStableIds(hasStableIds)
    }

    /** The [RecyclerView.Adapter.onViewRecycled] is managed by the FastAdapter so forward this correctly */
    override fun onViewRecycled(holder: VH) {
        adapter?.onViewRecycled(holder)
    }

    /** The [RecyclerView.Adapter.onFailedToRecycleView] is managed by the FastAdapter so forward this correctly */
    override fun onFailedToRecycleView(holder: VH): Boolean {
        return adapter?.onFailedToRecycleView(holder) ?: false
    }

    /** The [RecyclerView.Adapter.onViewDetachedFromWindow] is managed by the FastAdapter so forward this correctly */
    override fun onViewDetachedFromWindow(holder: VH) {
        adapter?.onViewDetachedFromWindow(holder)
    }

    /** The [RecyclerView.Adapter.onViewAttachedToWindow] is managed by the FastAdapter so forward this correctly */
    override fun onViewAttachedToWindow(holder: VH) {
        adapter?.onViewAttachedToWindow(holder)
    }

    /** The [RecyclerView.Adapter.onAttachedToRecyclerView] is managed by the FastAdapter so forward this correctly */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        adapter?.onAttachedToRecyclerView(recyclerView)
    }

    /** The [RecyclerView.Adapter.onDetachedFromRecyclerView] is managed by the FastAdapter so forward this correctly */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        adapter?.onDetachedFromRecyclerView(recyclerView)
    }
}
