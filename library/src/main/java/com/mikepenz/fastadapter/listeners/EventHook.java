package com.mikepenz.fastadapter.listeners;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface EventHook {
    @Nullable View onBind(@NonNull RecyclerView.ViewHolder viewHolder);
}
