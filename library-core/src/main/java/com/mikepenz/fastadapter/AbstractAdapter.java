package com.mikepenz.fastadapter;

import javax.annotation.Nullable;

/**
 * Created by mikepenz on 27.12.15.
 */
public abstract class AbstractAdapter<Item extends IItem> implements IAdapter<Item> {
    //keep a reference to the FastAdapter which contains the base logic
    protected FastAdapter<Item> mFastAdapter;

    /**
     * set the FastAdapter which will handle this AbstractAdapter
     * @param fastAdapter
     * @return
     */
    public AbstractAdapter<Item> withFastAdapter(FastAdapter<Item> fastAdapter) {
        this.mFastAdapter = fastAdapter;
        return this;
    }

    /**
     * @return the reference to the FastAdapter
     */
    @Override
    public FastAdapter<Item> getFastAdapter() {
        return mFastAdapter;
    }

    protected int mOrder = -1;

    /**
     * returs the position of this Adapter in the FastAdapter
     *
     * @return the position of this Adapter in the FastAdapter
     */
    public int getOrder() {
        return mOrder;
    }

    /**
     * sets the position of this Adapter in the FastAdapter
     * @param order the position of this Adapter in the FastAdapter
     */
    public void setOrder(int order) {
        this.mOrder = order;
    }

    /**
     * internal mapper to remember and add possible types for the RecyclerView
     *
     * @param items
     */
    public void mapPossibleTypes(@Nullable Iterable<Item> items) {
        if (items != null && mFastAdapter != null) {
            for (Item item : items) {
                mFastAdapter.registerTypeInstance(item);
            }
        }
    }
}
