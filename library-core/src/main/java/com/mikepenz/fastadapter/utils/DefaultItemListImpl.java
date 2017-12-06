package com.mikepenz.fastadapter.utils;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemList;

import java.util.ArrayList;
import java.util.List;

/**
 * The default item list implementation
 */

public class DefaultItemListImpl<Item extends IItem> implements IItemList<Item> {

    private ArrayList<Item> mItems = new ArrayList<>();

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
    }

    @Override
    public int removeRange(int position, int itemCount, int preItemCount) {
        //global position to relative
        int length = mItems.size();
        //make sure we do not delete to many items
        int saveItemCount = Math.min(itemCount, length - position + preItemCount);

        for (int i = 0; i < saveItemCount; i++) {
            mItems.remove(position - preItemCount);
        }
        return saveItemCount;
    }

    @Override
    public void move(int fromPosition, int toPosition, int preItemCount) {
        Item item = mItems.get(fromPosition - preItemCount);
        mItems.remove(fromPosition - preItemCount);
        mItems.add(toPosition - preItemCount, item);
    }

    @Override
    public int size() {
        return mItems.size();
    }

    @Override
    public void clear() {
        mItems.clear();
    }

    @Override
    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    @Override
    public void set(int position, Item item) {
        mItems.set(position, item);
    }

    @Override
    public void addAll(List<Item> items) {
        mItems.addAll(items);
    }

    @Override
    public void addAll(int position, List<Item> items) {
        mItems.addAll(position, items);
    }

    @Override
    public void setNewList(List<Item> items) {
        mItems = new ArrayList<>(items);
    }
}
