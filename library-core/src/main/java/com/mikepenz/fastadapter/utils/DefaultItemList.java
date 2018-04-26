package com.mikepenz.fastadapter.utils;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemList;

/**
 * The default item list
 */

public abstract class DefaultItemList<Item extends IItem> implements IItemList<Item> {

    private FastAdapter<Item> fastAdapter;

    public void setFastAdapter(FastAdapter<Item> fastAdapter) {
        this.fastAdapter = fastAdapter;
    }

    public FastAdapter<Item> getFastAdapter() {
        return fastAdapter;
    }
}
