package com.mikepenz.fastadapter.utils;

import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.IItem;

import java.util.HashMap;
import java.util.Map;
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
            HashMap<Integer, Stack<RecyclerView.ViewHolder>> cache = new HashMap<>();
            for (Item d : items) {
                if (!cache.containsKey(d.getType())) {
                    cache.put(d.getType(), new Stack<RecyclerView.ViewHolder>());
                }

                if (mCacheSize == -1 || cache.get(d.getType()).size() <= mCacheSize) {
                    cache.get(d.getType()).push(d.getViewHolder(recyclerView));
                }

                RecyclerView.RecycledViewPool recyclerViewPool = new RecyclerView.RecycledViewPool();

                //we fill the pool
                for (Map.Entry<Integer, Stack<RecyclerView.ViewHolder>> entry : cache.entrySet()) {
                    recyclerViewPool.setMaxRecycledViews(entry.getKey(), mCacheSize);

                    for (RecyclerView.ViewHolder holder : entry.getValue()) {
                        recyclerViewPool.putRecycledView(holder);
                    }

                    //make sure to clear the stack
                    entry.getValue().clear();
                }

                //make sure to clear the cache
                cache.clear();

                recyclerView.setRecycledViewPool(recyclerViewPool);
            }
        }
    }
}
