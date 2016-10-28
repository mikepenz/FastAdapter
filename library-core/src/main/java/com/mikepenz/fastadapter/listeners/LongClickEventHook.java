package com.mikepenz.fastadapter.listeners;

import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;

public abstract class LongClickEventHook<Item extends IItem> implements EventHook {
    public abstract boolean onLongClick(View v, int position, FastAdapter<Item> fastAdapter, Item item);
}