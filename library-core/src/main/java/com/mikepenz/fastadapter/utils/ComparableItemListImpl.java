package com.mikepenz.fastadapter.utils;

import android.support.annotation.Nullable;

import com.mikepenz.fastadapter.IItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The default item list implementation
 */

public class ComparableItemListImpl<Item extends IItem> extends DefaultItemListImpl<Item> {

    private Comparator<Item> mComparator;

    public ComparableItemListImpl(@Nullable Comparator<Item> comparator) {
        this.mItems = new ArrayList<>();
        this.mComparator = comparator;
    }

    public ComparableItemListImpl(@Nullable Comparator<Item> comparator, List<Item> items) {
        this.mItems = items;
        this.mComparator = comparator;
    }

    /**
     * @return the defined Comparator used for this ItemAdaper
     */
    public Comparator<Item> getComparator() {
        return mComparator;
    }

    /**
     * define a comparator which will be used to sort the list "everytime" it is altered
     * NOTE this will only sort if you "set" a new list or "add" new items (not if you provide a position for the add function)
     *
     * @param comparator used to sort the list
     * @return this
     */
    public ComparableItemListImpl<Item> withComparator(@Nullable Comparator<Item> comparator) {
        return withComparator(comparator, true);
    }

    /**
     * define a comparator which will be used to sort the list "everytime" it is altered
     * NOTE this will only sort if you "set" a new list or "add" new items (not if you provide a position for the add function)
     *
     * @param comparator used to sort the list
     * @param sortNow    specifies if we use the provided comparator to sort now
     * @return this
     */
    public ComparableItemListImpl<Item> withComparator(@Nullable Comparator<Item> comparator, boolean sortNow) {
        this.mComparator = comparator;

        //we directly sort the list with the defined comparator
        if (mItems != null && mComparator != null && sortNow) {
            Collections.sort(mItems, mComparator);
            getFastAdapter().notifyAdapterDataSetChanged();
        }

        return this;
    }

    @Override
    public void move(int fromPosition, int toPosition, int preItemCount) {
        Item item = mItems.get(fromPosition - preItemCount);
        mItems.remove(fromPosition - preItemCount);
        mItems.add(toPosition - preItemCount, item);
        if (mComparator != null) {
            Collections.sort(mItems, mComparator);
        }
        getFastAdapter().notifyAdapterDataSetChanged();
    }

    @Override
    public void addAll(List<Item> items, int preItemCount) {
        mItems.addAll(items);
        if (mComparator != null) {
            Collections.sort(mItems, mComparator);
        }
        getFastAdapter().notifyAdapterDataSetChanged();
    }

    @Override
    public void addAll(int position, List<Item> items, int preItemCount) {
        mItems.addAll(position - preItemCount, items);
        if (mComparator != null) {
            Collections.sort(mItems, mComparator);
        }
        getFastAdapter().notifyAdapterDataSetChanged();
    }

    @Override
    public void setNewList(List<Item> items, boolean notify) {
        mItems = new ArrayList<>(items);
        if (mComparator != null) {
            Collections.sort(mItems, mComparator);
        }
        if(notify) {
            getFastAdapter().notifyAdapterDataSetChanged();
        }
    }
}
