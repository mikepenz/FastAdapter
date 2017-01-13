package com.mikepenz.fastadapter.listeners;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;

import java.util.List;

public abstract class CustomEventHook<Item extends IItem> implements EventHook<Item> {
    public abstract void onEvent(View view, RecyclerView.ViewHolder viewHolder, FastAdapter<Item> fastAdapter);

    @Nullable
    @Override
    public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
        return null;
    }

    @Nullable
    @Override
    public List<View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder) {
        return null;
    }
}