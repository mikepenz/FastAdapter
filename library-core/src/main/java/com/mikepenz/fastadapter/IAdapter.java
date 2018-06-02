package com.mikepenz.fastadapter;

import java.util.List;

import javax.annotation.Nullable;

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
     * defines the FastAdapter which manages all the core logic
     *
     */
    IAdapter<Item> withFastAdapter(FastAdapter<Item> fastAdapter);

    /**
     * internal mapper to remember and add possible types for the RecyclerView
     *
     * @param items
     */
    void mapPossibleTypes(@Nullable Iterable<Item> items);

    /**
     * returs the position of this Adapter in the FastAdapter
     *
     * @return the position of this Adapter in the FastAdapter
     */
    int getOrder();

    /**
     * sets the position of this Adapter in the FastAdapter
     */
    void setOrder(int order);

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
     * Searches for the given item and calculates its relative position
     *
     * @param item the item which is searched for
     * @return the relative position
     */
    int getAdapterPosition(Item item);

    /**
     * Searches for the given item and calculates its relative position
     *
     * @param identifier the identifier of an item which is searched for
     * @return the relative position
     */
    int getAdapterPosition(long identifier);


    /**
     * Returns the global position based on the relative position given
     *
     * @param position the relative position within this adapter
     * @return the global position used for all methods
     */
    int getGlobalPosition(int position);
}
