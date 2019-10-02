package com.mikepenz.fastadapter

import androidx.recyclerview.widget.RecyclerView

interface ISuperDelegate {
    /**
     * finds the int ItemViewType from the IItem which exists at the given position
     *
     * @param position the global position
     * @return the viewType for this position
     */
    fun superGetItemViewType(position: Int): Int

    /**
     * finds the int ItemId from the IItem which exists at the given position
     *
     * @param position the global position
     * @return the itemId for this position
     */
    fun superGetItemId(position: Int): Long

    /**
     * Binds the data to the created ViewHolder and sets the listeners to the holder.itemView
     * Note that you should use the `onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads`
     * as it allows you to implement a more efficient adapter implementation
     *
     * @param holder   the viewHolder we bind the data on
     * @param position the global position
     */
    fun superOnBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>)

    /**
     * Unbinds the data to the already existing ViewHolder and removes the listeners from the holder.itemView
     *
     * @param holder the viewHolder we unbind the data from
     */
    fun superOnViewRecycled(holder: RecyclerView.ViewHolder)

    /**
     * is called in onViewDetachedFromWindow when the view is detached from the window
     *
     * @param holder the viewHolder for the view which got detached
     */
    fun superOnViewDetachedFromWindow(holder: RecyclerView.ViewHolder)

    /**
     * is called in onViewAttachedToWindow when the view is detached from the window
     *
     * @param holder the viewHolder for the view which got detached
     */
    fun superOnViewAttachedToWindow(holder: RecyclerView.ViewHolder)

    /**
     * is called when the ViewHolder is in a transient state. return true if you want to reuse
     * that view anyways
     *
     * @param holder the viewHolder for the view which failed to recycle
     * @return true if we want to recycle anyways (false - it get's destroyed)
     */
    fun superOnFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean

    fun superOnAttachedToRecyclerView(recyclerView: RecyclerView)

    fun superOnDetachedFromRecyclerView(recyclerView: RecyclerView)
}