package com.mikepenz.fastadapter;

import java.util.List;

/**
 * Created by mikepenz on 30.12.15.
 */
public interface IItemAdapter<Item extends IItem> extends IAdapter<Item> {

    /**
     * sets the subItems of the given collapsible
     *
     * @param collapsible the collapsible which gets the subItems set
     * @param subItems    the subItems for this collapsible item
     * @return the item type of the collapsible
     */
    <T> T setSubItems(IExpandable<T, Item> collapsible, List<Item> subItems);

    /**
     * set a new list of items and apply it to the existing list (clear - add) for this adapter
     *
     * @param items
     */
    IItemAdapter<Item> set(List<Item> items);

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param items
     */
    IItemAdapter<Item> setNewList(List<Item> items);

    /**
     * add an array of items to the end of the existing items
     *
     * @param items
     */
    IItemAdapter<Item> add(Item... items);

    /**
     * add a list of items to the end of the existing items
     *
     * @param items
     */
    IItemAdapter<Item> add(List<Item> items);

    /**
     * add an array of items at the given position within the existing items
     *
     * @param position the global position
     * @param items
     */
    IItemAdapter<Item> add(int position, Item... items);

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param items
     */
    IItemAdapter<Item> add(int position, List<Item> items);

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param item
     */
    IItemAdapter<Item> set(int position, Item item);

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the global position
     */
    IItemAdapter<Item> remove(int position);

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount
     */
    IItemAdapter<Item> removeRange(int position, int itemCount);

    /**
     * removes all items of this adapter
     */
    IItemAdapter<Item> clear();

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
