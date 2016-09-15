package com.mikepenz.fastadapter_extensions.utilities;

import android.support.v4.util.Pair;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Set;

/**
 * Created by flisar on 15.09.2016.
 */
public class SubItemUtils
{
    /**
     * counts the items in the adapter, respecting subitems regardless of there current visibility
     *
     * @param countHeaders      if true, headers will be counted as well
     * @return number of items in the adapter
     */
    public static int countItems(final FastItemAdapter adapter, boolean countHeaders) {
        return countItems(adapter, adapter.getAdapterItems(), countHeaders, false);
    }

    private static int countItems(final FastItemAdapter adapter, List<IItem> items, boolean countHeaders, boolean subItemsOnly) {
        if (items == null || items.size() == 0)
            return 0;

        int count = 0;
        int itemCount = items.size();
        IItem item;
        List<IItem> subItems;
        for (int i = 0; i < itemCount; i++) {
            item = items.get(i);
            if (item instanceof IExpandable && ((IExpandable)item).getSubItems() != null) {
                subItems = ((IExpandable)item).getSubItems();
                count += subItems != null ? subItems.size() : 0;
                count += countItems(adapter, subItems, countHeaders, true);
                if (countHeaders)
                    count++;
            }
            // in some cases, we must manually check, if the item is a sub item, process is optimised as much as possible via the subItemsOnly parameter already
            // sub items will be counted in above if statement!
            else if (!subItemsOnly && getParent(adapter, item, adapter.getAdapterPosition(item)) == null)
                count++;
        }
        return count;
    }

    // TODO: this is the bottleneck in this class, this may be slow in big lists!
    // IMPROVEMENT POSSIBLE?
    private static Pair<IItem, Integer> getParent(FastItemAdapter adapter, IItem item, int itemPosition) {
        int parentIndex = itemPosition - 1;
        IItem parent;
        List<IItem> subItems;
        while (parentIndex >= 0) {
            parent = adapter.getAdapterItem(parentIndex);
            if (parent instanceof IExpandable && ((IExpandable)parent).getSubItems() != null) {
                // it is possible, that normal items are positioned between expandable ones
                // so following check is necessary
                if (((IExpandable)parent).getSubItems().contains(item))
                    return new Pair<>(parent, parentIndex);
                else
                    return null;
            }
            parentIndex--;
        }
        return null;
    }

    /**
     * deletes all selected items from the adapter respecting if the are sub items or not
     * subitems are removed from their parents sublists, main items are directly removed
     *
     * @param deleteEmptyHeaders      if true, empty headers will be removed from the adapter
     * @return List of items that have been removed from the adapter
     */
    public static List<IItem> deleteSelected(final FastItemAdapter adapter, boolean notifyParent, boolean deleteEmptyHeaders) {
        List<IItem> deleted = new ArrayList<>();

        // we use a LinkedList, because this has performance advantages when modifying the listIterator during iteration!
        // Modifying list is O(1)
        LinkedList<IItem> selectedItems = new LinkedList<>(adapter.getSelectedItems());

        // we delete item per item from the adapter directly or from the parent
        // if keepEmptyHeaders is false, we add empty headers to the selected items set via the iterator, so that they are processed in the loop as well
        IItem item, parent;
        int pos, parentIndex;
        boolean expanded;
        ListIterator<IItem> it = selectedItems.listIterator();
        while(it.hasNext()){
            item = it.next();
            pos = adapter.getPosition(item);

            // search for parent - if we find one, we remove the item from the parent's subitems directly
            Pair<IItem, Integer> parentData = getParent(adapter, item, pos);
            if (parentData != null) {
                ((IExpandable)parentData.first).getSubItems().remove(item);
                adapter.notifyAdapterSubItemsChanged(parentData.second, ((IExpandable)parentData.first).getSubItems().size() + 1);
                if (notifyParent) {
                    expanded = ((IExpandable)parentData.first).isExpanded();
                    adapter.notifyAdapterItemChanged(parentData.second);
                    // expand the item again if it was expanded before calling notifyAdapterItemChanged
                    if (expanded)
                        adapter.expand(parentData.second);
                }

                deleted.add(item);

                if (deleteEmptyHeaders && ((IExpandable)parentData.first).getSubItems().size() == 0) {
                    it.add(parentData.first);
                    it.previous();
                }
            } else {
                // if we did not find a parent, we remove the item from the adapter
                adapter.remove(pos);
                deleted.add(item);
            }
        }

        return deleted;
    }
}
