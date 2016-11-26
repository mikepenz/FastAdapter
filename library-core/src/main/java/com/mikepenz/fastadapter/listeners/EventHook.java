package com.mikepenz.fastadapter.listeners;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.IItem;

import java.util.List;

public interface EventHook<Item extends IItem> {
    @Nullable
    View onBind(@NonNull RecyclerView.ViewHolder viewHolder);

    @Nullable
    List<? extends View> onBindMany(@NonNull RecyclerView.ViewHolder viewHolder);
}
