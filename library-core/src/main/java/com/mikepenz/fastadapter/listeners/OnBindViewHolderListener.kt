package com.mikepenz.fastadapter.listeners

import androidx.recyclerview.widget.RecyclerView

interface OnBindViewHolderListener {
    /**
     * is called in onBindViewHolder to bind the data on the ViewHolder
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     * @param payloads   the payloads provided by the adapter
     */
    fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>)

    /**
     * is called in onViewRecycled to unbind the data on the ViewHolder
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     */
    fun unBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int)

    /**
     * is called in onViewAttachedToWindow when the view is detached from the window
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     */
    fun onViewAttachedToWindow(viewHolder: RecyclerView.ViewHolder, position: Int)

    /**
     * is called in onViewDetachedFromWindow when the view is detached from the window
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     */
    fun onViewDetachedFromWindow(viewHolder: RecyclerView.ViewHolder, position: Int)

    /**
     * is called when the ViewHolder is in a transient state. return true if you want to reuse
     * that view anyways
     *
     * @param viewHolder the viewHolder for the view which failed to recycle
     * @return true if we want to recycle anyways (false - it get's destroyed)
     */
    fun onFailedToRecycleView(viewHolder: RecyclerView.ViewHolder, position: Int): Boolean
}
