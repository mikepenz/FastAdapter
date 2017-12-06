package com.mikepenz.fastadapter.utils;

import com.mikepenz.fastadapter.FastAdapter;

/**
 * The default list update callback implementation
 */

public class DefaultListUpdateCallbackImpl extends DefaultListUpdateCallback {

    @Override
    public void onInserted(int position, int count) {
        FastAdapter fastAdapter = getFastAdapter();
        if (fastAdapter == null) return;
        fastAdapter.notifyAdapterItemRangeInserted(position, count);
    }

    @Override
    public void onRemoved(int position, int count) {
        FastAdapter fastAdapter = getFastAdapter();
        if (fastAdapter == null) return;
        fastAdapter.notifyAdapterItemRangeRemoved(position, count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        FastAdapter fastAdapter = getFastAdapter();
        if (fastAdapter == null) return;
        fastAdapter.notifyAdapterItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onChanged(int position, int count, Object payload) {
        FastAdapter fastAdapter = getFastAdapter();
        if (fastAdapter == null) return;
        fastAdapter.notifyAdapterItemRangeChanged(position, count, payload);
    }
}
