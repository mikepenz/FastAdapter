package com.mikepenz.fastadapter.listeners;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;

public abstract class CustomEventHook<Item extends IItem> implements EventHook {
    public abstract void onEvent(FastAdapter<Item> fastAdapter, RecyclerView.ViewHolder viewHolder, View view);
}