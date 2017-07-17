package com.mikepenz.fastadapter.listeners;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;

public interface OnCreateViewHolderListener<Item extends IItem> {
    /**
     * is called inside the onCreateViewHolder method and creates the viewHolder based on the provided viewTyp
     *
     * @param fastAdapter the fastAdapter which handles the creation of this viewHolder
     * @param parent   the parent which will host the View
     * @param viewType the type of the ViewHolder we want to create
     * @return the generated ViewHolder based on the given viewType
     */
    RecyclerView.ViewHolder onPreCreateViewHolder(FastAdapter<Item> fastAdapter, ViewGroup parent, int viewType);

    /**
     * is called after the viewHolder was created and the default listeners were added
     *
     * @param fastAdapter the fastAdapter which handles the creation of this viewHolder
     * @param viewHolder the created viewHolder after all listeners were set
     * @return the viewHolder given as param
     */
    RecyclerView.ViewHolder onPostCreateViewHolder(FastAdapter<Item> fastAdapter, RecyclerView.ViewHolder viewHolder);
}
