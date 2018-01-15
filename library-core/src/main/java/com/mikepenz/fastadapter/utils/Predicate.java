package com.mikepenz.fastadapter.utils;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;

/**
 * Predicate interface to be used with the recursive method.
 */
public interface Predicate<Item extends IItem> {
    /**
     * `apply` is called for every single item in the `recursive` method.
     *
     * @param adapter  the FastAdapter instance, if
     * @param item     the item to check
     * @param position the position of the item, or "-1" if it is a non displayed sub item
     * @return true if we matched and no longer want to continue (will be ignored if `stopOnMatch` of the recursive function is false)
     */
    boolean apply(FastAdapter<Item> adapter, Item item, int position);
}