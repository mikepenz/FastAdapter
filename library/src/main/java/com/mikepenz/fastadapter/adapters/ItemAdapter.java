package com.mikepenz.fastadapter.adapters;

import com.mikepenz.fastadapter.AbstractAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.utils.IdDistributor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by mikepenz on 27.12.15.
 * A general ItemAdapter implementation based on the AbstractAdapter to speed up development for general items
 * This adapter has the order of 500 which is the centered order
 */
public class ItemAdapter extends AbstractAdapter {
    //the items handled and managed by this item
    private List<IItem> mItems = new ArrayList<>();

    //defines if the IdDistributor is used to set ID's to all added items
    private boolean mUseIdDistributor = true;

    /**
     * defines if the IdDistributor is used to provide an ID to all added items which do not yet define an id
     *
     * @param useIdDistributor false if the IdDistributor shouldn't be used
     * @return this
     */
    public ItemAdapter withUseIdDistributor(boolean useIdDistributor) {
        this.mUseIdDistributor = useIdDistributor;
        return this;
    }

    /**
     * @return the order of the items within the FastAdapter
     */
    @Override
    public int getOrder() {
        return 500;
    }

    /**
     * @return the count of items within this adapter
     */
    @Override
    public int getAdapterItemCount() {
        return mItems.size();
    }

    /**
     * @param position the relative position
     * @return the item inside this adapter
     */
    @Override
    public IItem getAdapterItem(int position) {
        return mItems.get(position);
    }

    /**
     * @return the items defined in this adapter
     */
    public List<IItem> getItems() {
        return mItems;
    }

    /**
     * set a new list of items for this adapter
     *
     * @param items
     */
    public void set(List<IItem> items) {
        if (mUseIdDistributor) {
            IdDistributor.checkIds(items);
        }
        mItems = items;
        mapPossibleTypes(mItems);
        getFastAdapter().notifyAdapterItemRangeChanged(getFastAdapter().getItemCount(getOrder()), getAdapterItemCount());
    }

    /**
     * add an array of items to the end of the existing items
     *
     * @param items
     */
    public void add(IItem... items) {
        if (items != null) {
            if (mUseIdDistributor) {
                IdDistributor.checkIds(items);
            }
            Collections.addAll(mItems, items);
            mapPossibleTypes(Arrays.asList(items));
            getFastAdapter().notifyAdapterItemRangeInserted(getFastAdapter().getItemCount(getOrder()), items.length);
        }
    }

    /**
     * add a list of items to the end of the existing items
     *
     * @param items
     */
    public void add(List<IItem> items) {
        if (items != null) {
            if (mUseIdDistributor) {
                IdDistributor.checkIds(items);
            }
            mItems.addAll(items);
            mapPossibleTypes(items);
            getFastAdapter().notifyAdapterItemRangeInserted(getFastAdapter().getItemCount(getOrder()), items.size());
        }
    }

    /**
     * add an array of items at the given position within the existing items
     *
     * @param position the relative position (position of this adapter)
     * @param items
     */
    public void add(int position, IItem... items) {
        if (mUseIdDistributor) {
            IdDistributor.checkIds(items);
        }
        if (items != null) {
            mItems.addAll(position, Arrays.asList(items));
            mapPossibleTypes(Arrays.asList(items));
            getFastAdapter().notifyAdapterItemRangeInserted(getFastAdapter().getItemCount(getOrder()) + position + 1, items.length);
        }
    }

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the relative position (position of this adapter)
     * @param items
     */
    public void add(int position, List<IItem> items) {
        if (mUseIdDistributor) {
            IdDistributor.checkIds(items);
        }
        if (items != null) {
            mItems.addAll(position, items);
            mapPossibleTypes(items);
            getFastAdapter().notifyAdapterItemRangeInserted(getFastAdapter().getItemCount(getOrder()) + position + 1, items.size());
        }
    }

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the relative position (position of this adapter)
     * @param item
     */
    public void set(int position, IItem item) {
        if (mUseIdDistributor) {
            IdDistributor.checkId(item);
        }
        mItems.set(position, item);
        mapPossibleType(item);
        getFastAdapter().notifyAdapterItemChanged(getFastAdapter().getItemCount(getOrder()) + position);
    }

    /**
     * add an item at the end of the existing items
     *
     * @param item
     */
    public void add(IItem item) {
        if (mUseIdDistributor) {
            IdDistributor.checkId(item);
        }
        mItems.add(item);
        mapPossibleType(item);
        getFastAdapter().notifyAdapterItemInserted(getFastAdapter().getItemCount(getOrder()) + mItems.size());
    }

    /**
     * add an item at the given position within the existing icons
     *
     * @param position the relative position (position of this adapter)
     * @param item
     */
    public void add(int position, IItem item) {
        if (mUseIdDistributor) {
            IdDistributor.checkId(item);
        }
        mItems.add(position, item);
        mapPossibleType(item);
        getFastAdapter().notifyAdapterItemInserted(getFastAdapter().getItemCount(getOrder()) + position);
    }

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the relative position (position of this adapter)
     */
    public void remove(int position) {
        mItems.remove(position);
        getFastAdapter().notifyAdapterItemRemoved(getFastAdapter().getItemCount(getOrder()) + position);
    }

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the relative position (position of this adapter)
     * @param itemCount
     */
    public void removeItemRange(int position, int itemCount) {
        int length = mItems.size();
        //make sure we do not delete to many items
        int saveItemCount = Math.min(itemCount, length - position - 1);

        for (int i = 0; i < saveItemCount; i++) {
            mItems.remove(position);
        }

        getFastAdapter().notifyAdapterItemRangeRemoved(getFastAdapter().getItemCount(getOrder()) + position, saveItemCount);
    }

    /**
     * removes all items of this adapter
     */
    public void clear() {
        int count = mItems.size();
        mItems.clear();
        getFastAdapter().notifyAdapterItemRangeRemoved(getFastAdapter().getItemCount(getOrder()), count);
    }
}
