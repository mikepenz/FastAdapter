package com.mikepenz.fastadapter.listeners;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.utils.EventHookUtil;

/**
 * default implementation of the OnCreateViewHolderListener
 */
public class OnCreateViewHolderListenerImpl<Item extends IItem> implements OnCreateViewHolderListener<Item> {
    /**
     * is called inside the onCreateViewHolder method and creates the viewHolder based on the provided viewTyp
     *
     * @param parent   the parent which will host the View
     * @param viewType the type of the ViewHolder we want to create
     * @return the generated ViewHolder based on the given viewType
     */
    @Override
    public RecyclerView.ViewHolder onPreCreateViewHolder(FastAdapter<Item> fastAdapter, ViewGroup parent, int viewType) {
        return fastAdapter.getTypeInstance(viewType).getViewHolder(parent);
    }

    /**
     * is called after the viewHolder was created and the default listeners were added
     *
     * @param viewHolder the created viewHolder after all listeners were set
     * @return the viewHolder given as param
     */
    @Override
    public RecyclerView.ViewHolder onPostCreateViewHolder(FastAdapter<Item> fastAdapter, RecyclerView.ViewHolder viewHolder) {
        EventHookUtil.bind(viewHolder, fastAdapter.getEventHooks());
        return viewHolder;
    }
}
