package com.mikepenz.fastadapter.commons.utils;

import android.support.annotation.Nullable;

import com.mikepenz.fastadapter.IItem;

/**
 * Created by mikepenz on 24.08.16.
 */

public class DiffCallbackImpl<Item extends IItem> implements DiffCallback<Item> {
    @Override
    public boolean areItemsTheSame(Item oldItem, Item newItem) {
        return oldItem.getIdentifier() == newItem.getIdentifier();
    }

    @Override
    public boolean areContentsTheSame(Item oldItem, Item newItem) {
        return oldItem.equals(newItem);
    }

    @Nullable
    @Override
    public Object getChangePayload(Item oldItem, int oldItemPosition, Item newItem, int newItemPosition) {
        return null;
    }
}
