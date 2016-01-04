package com.mikepenz.fastadapter;

import java.util.List;

/**
 * Created by mikepenz on 30.12.15.
 */
public interface IItemAdapter extends IAdapter {

    /**
     * set a new list of items for this adapter
     *
     * @param items
     */
    void set(List<IItem> items);

    /**
     * add an array of items to the end of the existing items
     *
     * @param items
     */
    void add(IItem... items);

    /**
     * add a list of items to the end of the existing items
     *
     * @param items
     */
    void add(List<IItem> items);

    /**
     * add an array of items at the given position within the existing items
     *
     * @param position the relative position (position of this adapter)
     * @param items
     */
    void add(int position, IItem... items);

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the relative position (position of this adapter)
     * @param items
     */
    void add(int position, List<IItem> items);

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the relative position (position of this adapter)
     * @param item
     */
    void set(int position, IItem item);

    /**
     * add an item at the end of the existing items
     *
     * @param item
     */
    void add(IItem item);

    /**
     * add an item at the given position within the existing icons
     *
     * @param position the relative position (position of this adapter)
     * @param item
     */
    void add(int position, IItem item);

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the relative position (position of this adapter)
     */
    void remove(int position);

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the relative position (position of this adapter)
     * @param itemCount
     */
    void removeItemRange(int position, int itemCount);

    /**
     * removes all items of this adapter
     */
    void clear();
}
