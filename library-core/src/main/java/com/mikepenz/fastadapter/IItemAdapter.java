package com.mikepenz.fastadapter;

import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by mikepenz on 30.12.15.
 */
public interface IItemAdapter<Model, Item extends IItem> extends IAdapter<Item> {

    /**
     * set a new list of items and apply it to the existing list (clear - add) for this adapter
     *
     * @param items
     */
    IItemAdapter<Model, Item> set(List<Model> items);

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param items
     */
    IItemAdapter<Model, Item> setNewList(List<Model> items);

    /**
     * add an array of items to the end of the existing items
     *
     * @param items
     */
    IItemAdapter<Model, Item> add(Model... items);

    /**
     * add a list of items to the end of the existing items
     *
     * @param items
     */
    IItemAdapter<Model, Item> add(List<Model> items);

    /**
     * add a list of items to the end of the existing items
     *
     * @param items
     */
    IItemAdapter<Model, Item> addInternal(List<Item> items);

    /**
     * add an array of items at the given position within the existing items
     *
     * @param position the global position
     * @param items
     */
    IItemAdapter<Model, Item> add(int position, Model... items);

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param items
     */
    IItemAdapter<Model, Item> add(int position, List<Model> items);

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param items
     */
    IItemAdapter<Model, Item> addInternal(int position, List<Item> items);

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param item
     */
    IItemAdapter<Model, Item> set(int position, Model item);

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param item
     */
    IItemAdapter<Model, Item> setInternal(int position, Item item);

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the global position
     */
    IItemAdapter<Model, Item> remove(int position);

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount
     */
    IItemAdapter<Model, Item> removeRange(int position, int itemCount);

    /**
     * removes all items of this adapter
     */
    IItemAdapter<Model, Item> clear();

    /**
     * the interface used to filter the list inside the ItemFilter
     */
    interface Predicate<Item extends IItem> {
        /**
         * @param item       the item which is checked if it should get filtered
         * @param constraint the string constraint used to filter items away
         * @return true if it should stay. false if it should get filtered away
         */
        boolean filter(Item item, @Nullable CharSequence constraint);
    }
}
