package com.mikepenz.fastadapter.adapters;

import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.IItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by mikepenz on 27.12.15.
 */
public class ItemAdapter extends AbstractAdapter {
    private List<IItem> mItems = new ArrayList<>();

    @Override
    public int getOrder() {
        return 500;
    }

    @Override
    public int getAdapterItemCount() {
        return mItems.size();
    }

    @Override
    public IItem getAdapterItem(int position) {
        return mItems.get(position);
    }

    public List<IItem> getDrawerItems() {
        return mItems;
    }

    public void set(List<IItem> items) {
        mItems = items;
        mapPossibleTypes(mItems);
        getBaseAdapter().notifyAdapterItemRangeChanged(getBaseAdapter().getItemCount(getOrder()), getAdapterItemCount());
    }

    public void add(IItem... items) {
        if (items != null) {
            Collections.addAll(mItems, items);
            mapPossibleTypes(Arrays.asList(items));
            getBaseAdapter().notifyAdapterItemRangeInserted(getBaseAdapter().getItemCount(getOrder()), items.length);
        }
    }

    public void add(List<IItem> items) {
        if (items != null) {
            mItems.addAll(items);
            mapPossibleTypes(items);
            getBaseAdapter().notifyAdapterItemRangeInserted(getBaseAdapter().getItemCount(getOrder()), items.size());
        }
    }

    public void add(int position, IItem... items) {
        if (items != null) {
            mItems.addAll(position, Arrays.asList(items));
            mapPossibleTypes(Arrays.asList(items));
            getBaseAdapter().notifyAdapterItemRangeInserted(getBaseAdapter().getItemCount(getOrder()) + position + 1, items.length);
        }
    }

    public void add(int position, List<IItem> items) {
        if (items != null) {
            mItems.addAll(position, items);
            mapPossibleTypes(items);
            getBaseAdapter().notifyAdapterItemRangeInserted(getBaseAdapter().getItemCount(getOrder()) + position + 1, items.size());
        }
    }

    public void set(int position, IItem item) {
        mItems.set(position, item);
        mapPossibleType(item);
        getBaseAdapter().notifyAdapterItemChanged(getBaseAdapter().getItemCount(getOrder()) + position);
    }

    public void add(IItem item) {
        mItems.add(item);
        mapPossibleType(item);
        getBaseAdapter().notifyAdapterItemInserted(getBaseAdapter().getItemCount(getOrder()) + mItems.size());
    }

    public void add(int position, IItem item) {
        mItems.add(position, item);
        mapPossibleType(item);
        getBaseAdapter().notifyAdapterItemInserted(getBaseAdapter().getItemCount(getOrder()) + position);
    }

    public void remove(int position) {
        mItems.remove(position);
        getBaseAdapter().notifyAdapterItemRemoved(getBaseAdapter().getItemCount(getOrder()) + position);
    }

    public void removeItemRange(int position, int itemCount) {
        int length = mItems.size();
        //make sure we do not delete to many items
        int saveItemCount = Math.min(itemCount, length - position - 1);

        for (int i = 0; i < saveItemCount; i++) {
            mItems.remove(position);
        }

        getBaseAdapter().notifyAdapterItemRangeRemoved(getBaseAdapter().getItemCount(getOrder()) + position, saveItemCount);
    }

    public void clear() {
        int count = mItems.size();
        mItems.clear();
        getBaseAdapter().notifyAdapterItemRangeRemoved(getBaseAdapter().getItemCount(getOrder()), count);
    }
}
