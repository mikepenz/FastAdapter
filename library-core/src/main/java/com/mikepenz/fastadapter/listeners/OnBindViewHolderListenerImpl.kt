package com.mikepenz.fastadapter.listeners

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.R

open class OnBindViewHolderListenerImpl<Item : GenericItem> : OnBindViewHolderListener {
    /**
     * Is called in onBindViewHolder to bind the data on the ViewHolder
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     * @param payloads   the payloads provided by the adapter
     */
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        val adapter = FastAdapter.getFromHolderTag<Item>(viewHolder) ?: return
        val item = adapter.getItem(position) ?: return
        (item as? IItem<RecyclerView.ViewHolder>)?.bindView(viewHolder, payloads)
        (viewHolder as? FastAdapter.ViewHolder<Item>)?.bindView(item, payloads)
        //set the R.id.fastadapter_item tag of this item to the item object (can be used when retrieving the view)
        viewHolder.itemView.setTag(R.id.fastadapter_item, item)
    }

    /**
     * Is called in onViewRecycled to unbind the data on the ViewHolder
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     */
    override fun unBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = FastAdapter.getHolderAdapterItemTag<IItem<RecyclerView.ViewHolder>>(viewHolder)
        if (item != null) {
            item.unbindView(viewHolder)
            (viewHolder as? FastAdapter.ViewHolder<Item>)?.unbindView(item as Item)
            //remove set tag's
            viewHolder.itemView.setTag(R.id.fastadapter_item, null)
            viewHolder.itemView.setTag(R.id.fastadapter_item_adapter, null)
        } else {
            Log.e(
                    "FastAdapter",
                    "The bindView method of this item should set the `Tag` on its itemView (https://github.com/mikepenz/FastAdapter/blob/develop/library-core/src/main/java/com/mikepenz/fastadapter/items/AbstractItem.kt#L22)"
            )
        }
    }

    /**
     * Is called in onViewAttachedToWindow when the view is detached from the window
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     */
    override fun onViewAttachedToWindow(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = FastAdapter.getHolderAdapterItem<IItem<RecyclerView.ViewHolder>>(viewHolder, position)
        if (item != null) {
            try {
                item.attachToWindow(viewHolder)
                (viewHolder as? FastAdapter.ViewHolder<Item>)?.attachToWindow(item as Item)
            } catch (e: AbstractMethodError) {
                Log.e("FastAdapter", e.toString())
            }
        }
    }

    /**
     * Is called in onViewDetachedFromWindow when the view is detached from the window
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     */
    override fun onViewDetachedFromWindow(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = FastAdapter.getHolderAdapterItemTag<IItem<RecyclerView.ViewHolder>>(viewHolder)
                ?: return
        item.detachFromWindow(viewHolder)
        (viewHolder as? FastAdapter.ViewHolder<Item>)?.detachFromWindow(item as Item)
    }

    /**
     * Is called when the ViewHolder is in a transient state. return true if you want to reuse
     * that view anyways
     *
     * @param viewHolder the viewHolder for the view which failed to recycle
     * @param position   the position of this viewHolder
     * @return true if we want to recycle anyways (false - it get's destroyed)
     */
    override fun onFailedToRecycleView(
            viewHolder: RecyclerView.ViewHolder,
            position: Int
    ): Boolean {
        val item = FastAdapter.getHolderAdapterItemTag<IItem<RecyclerView.ViewHolder>>(viewHolder)
                ?: return false
        var recycle = item.failedToRecycle(viewHolder)
        if (viewHolder is FastAdapter.ViewHolder<*>) {
            recycle = recycle || (viewHolder as FastAdapter.ViewHolder<Item>).failedToRecycle(item as Item)
        }
        return recycle
    }
}
