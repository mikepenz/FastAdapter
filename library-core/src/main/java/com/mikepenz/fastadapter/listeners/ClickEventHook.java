package com.mikepenz.fastadapter.listeners;

import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;

public abstract class ClickEventHook<Item extends IItem> implements EventHook {
    public abstract void onClick(View v, int position, FastAdapter<Item> fastAdapter, Item item);
}