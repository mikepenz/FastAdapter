package com.mikepenz.fastadapter;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by mikepenz on 04/06/2017.
 */

public interface IAdapterExtension<Item extends IItem> {
    IAdapterExtension<Item> init(FastAdapter<Item> fastAdapter);

    void withSavedInstanceState(@Nullable Bundle savedInstanceState, String prefix);

    void saveInstanceState(@Nullable Bundle savedInstanceState, String prefix);

    boolean onClick(View v, int pos, FastAdapter<Item> fastAdapter, Item item);

    boolean onLongClick(View v, int pos, FastAdapter<Item> fastAdapter, Item item);

    boolean onTouch(View v, MotionEvent event, int position, FastAdapter<Item> fastAdapter, Item item);

    void notifyAdapterDataSetChanged();

    void notifyAdapterItemRangeInserted(int position, int itemCount);

    void notifyAdapterItemRangeRemoved(int position, int itemCount);

    void notifyAdapterItemMoved(int fromPosition, int toPosition);

    void notifyAdapterItemRangeChanged(int position, int itemCount, @Nullable Object payload);

    void set(List<Item> items, boolean resetFilter);

    void performFiltering(@Nullable CharSequence constraint);
}
