package com.mikepenz.fastadapter;

import java.util.List;

/**
 * Created by mikepenz on 27.12.15.
 */
public interface IAdapter<Item extends IItem> {
    /**
     * defines the FastAdapter which manages all the core logic
     *
     * @return the FastAdapter specified for this IAdapter
     */
    FastAdapter<Item> getFastAdapter();

    /**
     * defines in which order this adapter should be hooked into the FastAdapter
     *
     * @return the order of this adapter
     */
    int getOrder();

    /**
     * defines the count of items of THIS adapter
     *
     * @return the count of items of THIS adapter
     */
    int getAdapterItemCount();

    /**
     * @return the list of defined items within THIS adapter
     */
    List<Item> getAdapterItems();

    /**
     * @param position the relative position
     * @return the item at the given relative position within this adapter
     */
    Item getAdapterItem(int position);

    /**
     * Searches for the given item and calculates it's relative position
     *
     * @param item the item which is searched for
     * @return the relative position
     */
    int getAdapterPosition(Item item);


    /**
     * Returns the global position based on the relative position given
     *
     * @param position the relative position within this adapter
     * @return the global position used for all methods
     */
    int getGlobalPosition(int position);

    /**
     * @return the global item count
     */
    int getItemCount();

    /**
     * @param position the global position
     * @return the global item based on the global position
     */
    Item getItem(int position);
}
