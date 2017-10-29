package com.mikepenz.fastadapter.listeners;

import android.support.v7.widget.RecyclerView;

import java.util.List;

public interface OnBindViewHolderListener {
    /**
     * is called in onBindViewHolder to bind the data on the ViewHolder
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     * @param payloads   the payloads provided by the adapter
     */
    void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position, List<Object> payloads);

    /**
     * is called in onViewRecycled to unbind the data on the ViewHolder
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     */
    void unBindViewHolder(RecyclerView.ViewHolder viewHolder, int position);

    /**
     * is called in onViewAttachedToWindow when the view is detached from the window
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     */
    void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder, int position);

    /**
     * is called in onViewDetachedFromWindow when the view is detached from the window
     *
     * @param viewHolder the viewHolder for the type at this position
     * @param position   the position of this viewHolder
     */
    void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder, int position);

    /**
     * is called when the ViewHolder is in a transient state. return true if you want to reuse
     * that view anyways
     *
     * @param viewHolder the viewHolder for the view which failed to recycle
     * @return true if we want to recycle anyways (false - it get's destroyed)
     */
    boolean onFailedToRecycleView(RecyclerView.ViewHolder viewHolder, int position);
}
