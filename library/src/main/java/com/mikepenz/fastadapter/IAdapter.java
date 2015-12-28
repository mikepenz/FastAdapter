package com.mikepenz.fastadapter;

/**
 * Created by mikepenz on 27.12.15.
 */
public interface IAdapter {
    /**
     * defines the FastAdapter which manages all the core logic
     *
     * @return the FastAdapter specified for this IAdapter
     */
    FastAdapter getFastAdapter();

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
     * @param position the relative position
     * @return the item at the given relative position within this adapter
     */
    IItem getAdapterItem(int position);

    /**
     * @return the global item count
     */
    int getItemCount();

    /**
     * @param position the global position
     * @return the global item based on the global position
     */
    IItem getItem(int position);
}
