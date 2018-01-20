package com.mikepenz.fastadapter.utils;

import com.mikepenz.fastadapter.IItem;

/**
 * AdapterPredicate interface to be used with the recursive method.
 */
public interface AdapterPredicate<Item extends IItem> {
    /**
     * `apply` is called for every single item in the `recursive` method.
     *
     * @param item     the item to check
     * @param position the position of the item, or "-1" if it is a non displayed sub item
     * @return true if we matched and no longer want to continue (will be ignored if `stopOnMatch` of the recursive function is false)
     */
    boolean apply(Item item, int position);
}