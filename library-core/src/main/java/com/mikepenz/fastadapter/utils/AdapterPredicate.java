package com.mikepenz.fastadapter.utils;

import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;

/**
 * AdapterPredicate interface to be used with the recursive method.
 */
public interface AdapterPredicate<Item extends IItem> {
    /**
     * `apply` is called for every single item in the `recursive` method.
     *
     * @param lastParentAdapter  the last `IAdapter` managing the last (visible) parent item (that might also be a parent of a parent, ..)
     * @param lastParentPosition the global position of the last (visible) parent item, holding this sub item (that might also be a parent of a parent, ..)
     * @param item               the item to check
     * @param position           the global position of the item, or "-1" if it is a non displayed sub item
     * @return true if we matched and no longer want to continue (will be ignored if `stopOnMatch` of the recursive function is false)
     */
    boolean apply(IAdapter<Item> lastParentAdapter, int lastParentPosition, Item item, int position);
}