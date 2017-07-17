package com.mikepenz.fastadapter.listeners;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;

import java.util.List;

import javax.annotation.Nullable;

public abstract class TouchEventHook<Item extends IItem> implements EventHook<Item> {
    public abstract boolean onTouch(View v, MotionEvent event, int position, FastAdapter<Item> fastAdapter, Item item);

    @Nullable
    @Override
    public View onBind(RecyclerView.ViewHolder viewHolder) {
        return null;
    }

    @Nullable
    @Override
    public List<View> onBindMany(RecyclerView.ViewHolder viewHolder) {
        return null;
    }
}
