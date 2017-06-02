package com.mikepenz.fastadapter.listeners;

import android.support.annotation.Nullable;

import com.mikepenz.fastadapter.IItem;

import java.util.List;

/**
 * interface for the ItemFilterListener
 */
public interface ItemFilterListener<Item extends IItem> {
    void itemsFiltered(@Nullable CharSequence constraint, @Nullable List<Item> results);

    void onReset();
}