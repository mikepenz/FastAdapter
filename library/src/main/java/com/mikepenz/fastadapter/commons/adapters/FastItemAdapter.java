package com.mikepenz.fastadapter.commons.adapters;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

/**
 * Created by mikepenz on 18.01.16.
 */
public class FastItemAdapter<Item extends IItem> extends FastAdapter<Item> {
    private final ItemAdapter<Item> mItemAdapter = new ItemAdapter<>();

    /**
     * ctor
     */
    public FastItemAdapter() {
        mItemAdapter.wrap(this);
    }

    /**
     * returns the internal created ItemAdapter
     *
     * @return the ItemAdapter used inside this FastItemAdapter
     */
    public ItemAdapter<Item> items() {
        return mItemAdapter;
    }
}
