package com.mikepenz.fastadapter.utils;

import com.mikepenz.fastadapter.IItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The default item list implementation
 */

public class ComparableItemListImpl<Item extends IItem> extends DefaultItemListImpl<Item> {

    private List<Item> mItems;

    private Comparator<Item> mComparator;

    public ComparableItemListImpl(Comparator<Item> comparator) {
        this.mItems = new ArrayList<>();
        this.mComparator = comparator;
    }

    public ComparableItemListImpl(Comparator<Item> comparator, List<Item> items) {
        this.mItems = items;
        this.mComparator = comparator;
    }

    @Override
    public void move(int fromPosition, int toPosition, int preItemCount) {
        Item item = mItems.get(fromPosition - preItemCount);
        mItems.remove(fromPosition - preItemCount);
        mItems.add(toPosition - preItemCount, item);
        Collections.sort(mItems, mComparator);
        getFastAdapter().notifyAdapterDataSetChanged();
    }

    @Override
    public void addAll(List<Item> items, int preItemCount) {
        mItems.addAll(items);
        Collections.sort(items, mComparator);
        getFastAdapter().notifyAdapterDataSetChanged();
    }

    @Override
    public void addAll(int position, List<Item> items, int preItemCount) {
        mItems.addAll(position - preItemCount, items);
        Collections.sort(items, mComparator);
        getFastAdapter().notifyAdapterDataSetChanged();
    }
}
