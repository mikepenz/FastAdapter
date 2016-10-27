package com.mikepenz.fastadapter.listeners;

import android.view.MotionEvent;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;

public abstract class TouchEventHook<Item extends IItem> implements EventHook {
    public abstract boolean onTouch(View v, MotionEvent event, int position, FastAdapter<Item> fastAdapter, Item item);
}