package com.mikepenz.fastadapter.utils;

import android.support.v7.util.ListUpdateCallback;

import com.mikepenz.fastadapter.FastAdapter;

/**
 * The default list update callback
 */

public abstract class DefaultListUpdateCallback implements ListUpdateCallback {

    private FastAdapter fastAdapter;

    public abstract void onDataSetChanged();

    public void setFastAdapter(FastAdapter fastAdapter) {
        this.fastAdapter = fastAdapter;
    }

    public FastAdapter getFastAdapter() {
        return fastAdapter;
    }

}
