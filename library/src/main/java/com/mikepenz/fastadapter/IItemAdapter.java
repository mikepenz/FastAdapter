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
    void set(List<? extends IItem> items);

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
    void add(List<? extends IItem> items);

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
    void add(int position, List<? extends IItem> items);

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


    /**
     * the interface used to filter the list inside the ItemFilter
     */
    interface Predicate<Item extends IItem> {
        /**
         * @param item       the item which is checked if it should get filtered
         * @param constraint the string constraint used to filter items away
         * @return false if it should stay. true if it should get filtered away
         */
        boolean filter(Item item, CharSequence constraint);
    }
}
