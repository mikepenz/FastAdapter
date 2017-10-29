package com.mikepenz.fastadapter.listeners;

import android.view.View;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;

public interface OnLongClickListener<Item extends IItem> {
    /**
     * the onLongClick event of a specific item inside the RecyclerView
     *
     * @param v        the view we clicked
     * @param adapter  the adapter which is responsible for the given item
     * @param item     the IItem which was clicked
     * @param position the global position
     * @return return true if the event was consumed, otherwise false
     */
    boolean onLongClick(View v, IAdapter<Item> adapter, Item item, int position);
}
