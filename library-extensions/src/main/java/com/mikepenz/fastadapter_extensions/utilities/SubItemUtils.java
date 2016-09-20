package com.mikepenz.fastadapter_extensions.utilities;

import android.support.v4.util.Pair;
import android.util.Log;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by flisar on 15.09.2016.
 */
public class SubItemUtils
{
    /**
     * counts the items in the adapter, respecting subitems regardless of there current visibility
     * ATTENTION: this function is slow on large lists
     *
     * @param adapter      the adapter instance
     * @param predicate      predicate against which each item will be checked before counting it
     * @return number of items in the adapter that apply to the predicate
     */
    public static int countItems(final FastItemAdapter adapter, IPredicate predicate) {
        return countItems(adapter, adapter.getAdapterItems(), true, false, predicate);
    }

    /**
     * counts the items in the adapter, respecting subitems regardless of there current visibility
     * * ATTENTION: this function is slow on large lists
     *
     * @param adapter      the adapter instance
     * @param countHeaders      if true, headers will be counted as well
     * @return number of items in the adapter
     */
    public static int countItems(final FastItemAdapter adapter, boolean countHeaders) {
        return countItems(adapter, adapter.getAdapterItems(), countHeaders, false, null);
    }

    private static int countItems(final FastItemAdapter adapter, List<IItem> items, boolean countHeaders, boolean subItemsOnly, IPredicate predicate) {
        if (items == null || items.size() == 0)
            return 0;

        int temp,  count = 0;
        int itemCount = items.size();
        IItem item;
        List<IItem> subItems;
        for (int i = 0; i < itemCount; i++) {
            item = items.get(i);
            if (item instanceof IExpandable && ((IExpandable)item).getSubItems() != null) {
                subItems = ((IExpandable)item).getSubItems();
                if (predicate == null) {
                    count += subItems != null ? subItems.size() : 0;
                    count += countItems(adapter, subItems, countHeaders, true, predicate);
                    if (countHeaders)
                        count++;
                } else {
                    temp = subItems != null ? subItems.size() : 0;
                    for (int j = 0; j < temp; j++) {
                        if (predicate.apply(subItems.get(j)))
                            count++;
                    }
                    if (countHeaders && predicate.apply(item))
                        count++;
                }
            }
            // in some cases, we must manually check, if the item is a sub item, process is optimised as much as possible via the subItemsOnly parameter already
            // sub items will be counted in above if statement!
            else if (!subItemsOnly && getParent(adapter, item, adapter.getAdapterPosition(item)) == null) {
                if (predicate == null)
                    count++;
                else if (predicate.apply(item))
                    count++;
            }

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

        Log.d("DELETE", "selectedItems: " + selectedItems.size());

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
                boolean success = ((IExpandable)parentData.first).getSubItems().remove(item);
                Log.d("DELETE", "success=" + success + " | deletedId=" + item.getIdentifier() + " | parentId=" + parentData.first.getIdentifier() + " (sub items: " + ((IExpandable)parentData.first).getSubItems().size() + ")");
                adapter.notifyAdapterSubItemsChanged(parentData.second, ((IExpandable)parentData.first).getSubItems().size() + 1);

                // TODO: this does not work correctly!!!!
//                if (notifyParent) {
//                    expanded = ((IExpandable)parentData.first).isExpanded();
//                    adapter.notifyAdapterItemChanged(parentData.second);
//                    // expand the item again if it was expanded before calling notifyAdapterItemChanged
//                    if (expanded)
//                        adapter.expand(parentData.second);
//                }

                deleted.add(item);

                if (deleteEmptyHeaders && ((IExpandable)parentData.first).getSubItems().size() == 0) {
                    it.add(parentData.first);
                    it.previous();
                }
            } else {
                // if we did not find a parent, we remove the item from the adapter
                boolean success = adapter.remove(pos) != null;
                boolean isHeader = item instanceof IExpandable && ((IExpandable)item).getSubItems() != null;
                Log.d("DELETE", "success=" + success + " | deletedId=" + item.getIdentifier() + "(" + (isHeader ? "EMPTY HEADER" : "ITEM WITHOUT HEADER") + ")");
                deleted.add(item);
            }
        }

        Log.d("DELETE", "deleted (incl. empty headers): " + deleted.size());

        return deleted;
    }

    /**
     * deletes all selected items from the adapter respecting if the are sub items or not
     * subitems are removed from their parents sublists, main items are directly removed
     *
     * @param deleteEmptyHeaders      if true, empty headers will be removed from the adapter
     * @return List of items that have been removed from the adapter
     */
    public static List<IItem> deleteSelectedTEST(final FastItemAdapter adapter, boolean notifyParent, boolean deleteEmptyHeaders) {
        List<IItem> deleted = new ArrayList<>();

        // ATTENTION: notifying parents while iterating and deleting items results in bugs!
        // So we do notify the remaining parents after deletions are finished - anyway more effective
        Set<IItem> changedParents = new HashSet<>();
        Set<IItem> parentsToDelete = new HashSet<>();
        Set<IItem> itemsToDelete = new HashSet<>();

        // 1) we delete all sub items from it's parent + save a set of parents that have been changed
        // we must notify the adapter after every change!
        IItem item, parent;
        int pos;
        boolean expanded;
        Iterator<IItem> it = adapter.getSelectedItems().iterator();
        while(it.hasNext()){
            item = it.next();
            pos = adapter.getPosition(item);

            // search for parent - if we find one, we remove the item from the parent's subitems directly
            Pair<IItem, Integer> parentData = getParent(adapter, item, pos);
            if (parentData != null) {
                ((IExpandable)parentData.first).getSubItems().remove(item);
                adapter.notifyAdapterSubItemsChanged(parentData.second, ((IExpandable)parentData.first).getSubItems().size() + 1);
                changedParents.add(parentData.first);
                deleted.add(item);

                Log.d("DELETE", "Changed Parent ADDED: " + pos + "|" + parentData.first);

                if (deleteEmptyHeaders && ((IExpandable)parentData.first).getSubItems().size() == 0) {
                    changedParents.remove(parentData.first);
                    parentsToDelete.add(parentData.first);
                    deleted.add(parentData.first);
                }
            } else {
                // if we did not find a parent, we remove the item from the adapter
                // we do this later! otherwise we have problems because of updating the temp sub items
                itemsToDelete.add(item);
            }
        }

        Log.d("DELETE", "Changed Parents: " + changedParents.size());
        Log.d("DELETE", "Deleted Parents: " + parentsToDelete.size());
        Log.d("DELETE", "Normal items to delete: " + itemsToDelete.size());
        Log.d("DELETE", "Deleted items (inkl. parents): " + deleted.size());

        // 2) delete empty parents
        Iterator<IItem> itParents = parentsToDelete.iterator();
        while (itParents.hasNext()) {
            parent = itParents.next();
            pos = adapter.getPosition(parent);
            adapter.remove(pos);
        }

        // 3) now we delete the normal items without parents as well
        it = itemsToDelete.iterator();
        while (it.hasNext()) {
            item = it.next();
            pos = adapter.getPosition(item);
            adapter.remove(pos);
        }

        // 4) if desired, notify the REMAINING parent adapters about changes so that they can update their layout
        if (notifyParent) {
            itParents = changedParents.iterator();
            while (itParents.hasNext()) {
                parent = itParents.next();
                pos = adapter.getPosition(parent);
                expanded = ((IExpandable)parent).isExpanded();
                adapter.notifyAdapterItemChanged(pos);
                // expand the item again if it was expanded before calling notifyAdapterItemChanged
                if (expanded)
                    adapter.expand(pos);
            }
        }

        return deleted;
    }

    public interface IPredicate<T>{
        boolean apply(T data);
    }
}
