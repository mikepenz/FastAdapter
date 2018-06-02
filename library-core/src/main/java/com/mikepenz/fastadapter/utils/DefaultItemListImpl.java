package com.mikepenz.fastadapter.utils;

import com.mikepenz.fastadapter.IAdapterNotifier;
import com.mikepenz.fastadapter.IItem;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * The default item list implementation
 */

public class DefaultItemListImpl<Item extends IItem> extends DefaultItemList<Item> {

    protected List<Item> mItems;

    public DefaultItemListImpl() {
        this(new ArrayList<Item>());
    }

    public DefaultItemListImpl(List<Item> items) {
        this.mItems = items;
    }

    @Override
    public Item get(int position) {
        return mItems.get(position);
    }

    @Override
    public List<Item> getItems() {
        return mItems;
    }

    @Override
    public int getAdapterPosition(long identifier) {
        for (int i = 0, size = mItems.size(); i < size; i++) {
            if (mItems.get(i).getIdentifier() == identifier) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void remove(int position, int preItemCount) {
        mItems.remove(position - preItemCount);
        if (getFastAdapter() != null) {
            getFastAdapter().notifyAdapterItemRemoved(position);
        }
    }

    @Override
    public void removeRange(int position, int itemCount, int preItemCount) {
        //global position to relative
        int length = mItems.size();
        //make sure we do not delete to many items
        int saveItemCount = Math.min(itemCount, length - position + preItemCount);

        for (int i = 0; i < saveItemCount; i++) {
            mItems.remove(position - preItemCount);
        }
        if (getFastAdapter() != null) {
            getFastAdapter().notifyAdapterItemRangeRemoved(position, saveItemCount);
        }
    }

    @Override
    public void move(int fromPosition, int toPosition, int preItemCount) {
        Item item = mItems.get(fromPosition - preItemCount);
        mItems.remove(fromPosition - preItemCount);
        mItems.add(toPosition - preItemCount, item);
        if (getFastAdapter() != null) {
            getFastAdapter().notifyAdapterItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public int size() {
        return mItems.size();
    }

    @Override
    public void clear(int preItemCount) {
        int size = mItems.size();
        mItems.clear();
        if (getFastAdapter() != null) {
            getFastAdapter().notifyAdapterItemRangeRemoved(preItemCount, size);
        }
    }

    @Override
    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    @Override
    public void set(int position, Item item, int preItemCount) {
        mItems.set(position - preItemCount, item);
        if (getFastAdapter() != null) {
            getFastAdapter().notifyAdapterItemChanged(position);
        }
    }

    @Override
    public void addAll(List<Item> items, int preItemCount) {
        int countBefore = mItems.size();
        mItems.addAll(items);
        if (getFastAdapter() != null) {
            getFastAdapter().notifyAdapterItemRangeInserted(preItemCount + countBefore, items.size());
        }
    }

    @Override
    public void addAll(int position, List<Item> items, int preItemCount) {
        mItems.addAll(position - preItemCount, items);
        if (getFastAdapter() != null) {
            getFastAdapter().notifyAdapterItemRangeInserted(position, items.size());
        }
    }

    @Override
    public void set(List<Item> items, int preItemCount, @Nullable IAdapterNotifier adapterNotifier) {
        //get sizes
        int newItemsCount = items.size();
        int previousItemsCount = mItems.size();

        //make sure the new items list is not a reference of the already mItems list
        if (items != mItems) {
            //remove all previous items
            if (!mItems.isEmpty()) {
                mItems.clear();
            }

            //add all new items to the list
            mItems.addAll(items);
        }
        if (getFastAdapter() == null) return;
        //now properly notify the adapter about the changes
        if (adapterNotifier == null) {
            adapterNotifier = IAdapterNotifier.DEFAULT;
        }
        adapterNotifier.notify(getFastAdapter(), newItemsCount, previousItemsCount, preItemCount);
    }

    @Override
    public void setNewList(List<Item> items, boolean notify) {
        mItems = new ArrayList<>(items);
        if (getFastAdapter() != null && notify) {
            getFastAdapter().notifyAdapterDataSetChanged();
        }
    }
}
