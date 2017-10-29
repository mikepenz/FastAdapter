package com.mikepenz.fastadapter.utils;

import android.util.SparseArray;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.ITypeInstanceCache;

/**
 * Created by fabianterhorst on 24.08.17.
 */

public class DefaultTypeInstanceCache<Item extends IItem> implements ITypeInstanceCache<Item> {

    // we remember all possible types so we can create a new view efficiently
    private final SparseArray<Item> mTypeInstances = new SparseArray<>();

    @Override
    public boolean register(Item item) {
        if (mTypeInstances.indexOfKey(item.getType()) < 0) {
            mTypeInstances.put(item.getType(), item);
            return true;
        }
        return false;
    }

    @Override
    public Item get(int type) {
        return mTypeInstances.get(type);
    }

    @Override
    public void clear() {
        mTypeInstances.clear();
    }
}
