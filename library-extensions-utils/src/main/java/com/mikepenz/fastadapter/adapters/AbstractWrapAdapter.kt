package com.mikepenz.fastadapter.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import java.util.*

/**
 * Created by mikepenz on 03.03.16.
 */
abstract class AbstractWrapAdapter<Item : IItem<VH>, VH : RecyclerView.ViewHolder>(items: List<Item>) : RecyclerView.Adapter<VH>() {
    //the items handled and managed by this item
    open var items: List<Item> = ArrayList()

    //private AbstractAdapter mParentAdapter;
    //keep a reference to the FastAdapter which contains the base logic
    /**
     * @return the reference to the FastAdapter
     */
    var adapter: RecyclerView.Adapter<VH>? = null
        private set

    init {
        this.items = items
    }

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

    /**
     * this method states if we should insert a custom element at the vien position
     *
     * @param position
     * @return
     */
    abstract fun shouldInsertItemAtPosition(position: Int): Boolean

    /**
     * this method calculates how many elements were already inserted before this position;
     *
     * @param position
     * @return
     */
    abstract fun itemInsertedBeforeCount(position: Int): Int


    /**
     * overwrite the registerAdapterDataObserver to correctly forward all events to the FastAdapter
     *
     * @param observer
     */
    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
        adapter?.registerAdapterDataObserver(observer)
    }

    /**
     * overwrite the unregisterAdapterDataObserver to correctly forward all events to the FastAdapter
     *
     * @param observer
     */
    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.unregisterAdapterDataObserver(observer)
        adapter?.unregisterAdapterDataObserver(observer)
    }

    /**
     * overwrite the getItemViewType to correctly return the value from the FastAdapter
     *
     * @param position
     * @return
     */
    override fun getItemViewType(position: Int): Int {
        return if (shouldInsertItemAtPosition(position)) {
            getItem(position)?.type ?: 0
        } else {
            adapter?.getItemViewType(position - itemInsertedBeforeCount(position)) ?: 0
        }
    }

    /**
     * overwrite the getItemId to correctly return the value from the FastAdapter
     *
     * @param position
     * @return
     */
    override fun getItemId(position: Int): Long {
        return if (shouldInsertItemAtPosition(position)) {
            getItem(position)?.identifier ?: 0
        } else {
            adapter?.getItemId(position - itemInsertedBeforeCount(position)) ?: 0
        }
    }

    /**
     * make sure we return the Item from the FastAdapter so we retrieve the item from all adapters
     *
     * @param position
     * @return
     */
    fun getItem(position: Int): Item? {
        return if (shouldInsertItemAtPosition(position)) {
            items[itemInsertedBeforeCount(position - 1)]
        } else null
    }

    /**
     * make sure we return the count from the FastAdapter so we retrieve the count from all adapters
     *
     * @return
     */
    override fun getItemCount(): Int {
        val itemCount = adapter?.itemCount ?: 0
        return itemCount + itemInsertedBeforeCount(itemCount)
    }

    /**
     * the onCreateViewHolder is managed by the FastAdapter so forward this correctly
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        //TODO OPTIMIZE
        for (item in items) {
            if (item.type == viewType) {
                return item.getViewHolder(parent)
            }
        }

        val adapter = this.adapter ?: throw RuntimeException("A adapter needs to be wrapped")
        return adapter.onCreateViewHolder(parent, viewType)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        //empty implementation as the one with the List payloads is already called
    }

    /**
     * the onBindViewHolder is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        if (shouldInsertItemAtPosition(position)) {
            getItem(position)?.bindView(holder, payloads)
        } else {
            adapter?.onBindViewHolder(holder, position - itemInsertedBeforeCount(position), payloads)
        }
    }

    /**
     * the setHasStableIds is managed by the FastAdapter so forward this correctly
     *
     * @param hasStableIds
     */
    override fun setHasStableIds(hasStableIds: Boolean) {
        adapter?.setHasStableIds(hasStableIds)
    }

    /**
     * the onViewRecycled is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    override fun onViewRecycled(holder: VH) {
        adapter?.onViewRecycled(holder)
    }

    /**
     * the onFailedToRecycleView is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     * @return
     */
    override fun onFailedToRecycleView(holder: VH): Boolean {
        return adapter?.onFailedToRecycleView(holder) ?: false
    }

    /**
     * the onViewDetachedFromWindow is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    override fun onViewDetachedFromWindow(holder: VH) {
        adapter?.onViewDetachedFromWindow(holder)
    }

    /**
     * the onViewAttachedToWindow is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    override fun onViewAttachedToWindow(holder: VH) {
        adapter?.onViewAttachedToWindow(holder)
    }

    /**
     * the onAttachedToRecyclerView is managed by the FastAdapter so forward this correctly
     *
     * @param recyclerView
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        adapter?.onAttachedToRecyclerView(recyclerView)
    }

    /**
     * the onDetachedFromRecyclerView is managed by the FastAdapter so forward this correctly
     *
     * @param recyclerView
     */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        adapter?.onDetachedFromRecyclerView(recyclerView)
    }
}
