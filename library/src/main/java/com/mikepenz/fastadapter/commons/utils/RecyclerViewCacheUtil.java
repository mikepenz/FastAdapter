package com.mikepenz.fastadapter.commons.utils;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

import com.mikepenz.fastadapter.IItem;

import java.util.Stack;

/**
 * Created by mikepenz on 18.09.15.
 * This util prefills the cache of the RecyclerView to allow fast lag-free scrolling with many different views
 */
public class RecyclerViewCacheUtil<Item extends IItem> {
    private int mCacheSize = 2;

    /**
     * define the amount of elements which should be cached for a specific item type
     *
     * @param cacheSize
     * @return
     */
    public RecyclerViewCacheUtil withCacheSize(int cacheSize) {
        mCacheSize = cacheSize;
        return this;
    }

    /**
     * init the cache on your own.
     *
     * @param recyclerView
     * @param items
     */
    public void apply(RecyclerView recyclerView, Iterable<Item> items) {
        if (items != null) {
            //we pre-create the views for our cache
            SparseArray<Stack<RecyclerView.ViewHolder>> cache = new SparseArray<>();
            for (Item d : items) {
                Stack<RecyclerView.ViewHolder> holders = cache.get(d.getType());
                if (holders == null) {
                    cache.put(d.getType(), new Stack<RecyclerView.ViewHolder>());
                } else if (mCacheSize == -1 || holders.size() <= mCacheSize) {
                    holders.push(d.getViewHolder(recyclerView));
                }

                RecyclerView.RecycledViewPool recyclerViewPool = new RecyclerView.RecycledViewPool();

                //we fill the pool
                for (int i = 0, length = cache.size(); i < length; i++) {
                    int key = cache.keyAt(i);
                    Stack<RecyclerView.ViewHolder> entry = cache.get(key);
                    recyclerViewPool.setMaxRecycledViews(key, mCacheSize);
                    for (RecyclerView.ViewHolder holder : entry) {
                        recyclerViewPool.putRecycledView(holder);
                    }
                    //make sure to clear the stack
                    entry.clear();

                }

                //make sure to clear the cache
                cache.clear();

                recyclerView.setRecycledViewPool(recyclerViewPool);
            }
        }
    }
}
