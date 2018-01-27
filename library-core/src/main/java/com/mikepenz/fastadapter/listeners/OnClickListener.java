package com.mikepenz.fastadapter.listeners;

import android.view.View;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;

import javax.annotation.Nullable;

public interface OnClickListener<Item extends IItem> {
    /**
     * the onClick event of a specific item inside the RecyclerView
     *
     * @param v        the view we clicked
     * @param adapter  the adapter which is responsible for the given item
     * @param item     the IItem which was clicked
     * @param position the global position
     * @return true if the event was consumed, otherwise false
     */
    boolean onClick(@Nullable View v, IAdapter<Item> adapter, Item item, int position);
}
