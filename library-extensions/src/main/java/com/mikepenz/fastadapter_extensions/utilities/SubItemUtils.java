package com.mikepenz.fastadapter_extensions.utilities;

import android.support.v4.util.Pair;
import android.util.Log;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.ISubItem;
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

/**
 * Created by flisar on 15.09.2016.
 */
public class SubItemUtils {

    /**
     * returns a set of selected items, regardless of their visibility
     *
     * @param adapter      the adapter instance
     * @return a set of all selected items and subitems
     */
    public static Set<IItem> getSelectedItems(FastAdapter adapter) {
        Set<IItem> selections = new HashSet<>();
        int length = adapter.getItemCount();
        List<IItem> items = new ArrayList<>();
        for (int i = 0; i < length; i++)
            items.add(adapter.getItem(i));
        updateSelectedItemsWithCollapsed(selections, items);
        return selections;
    }

    private static void updateSelectedItemsWithCollapsed(Set<IItem> selected, List<IItem> items) {
        int length = items.size();
        for (int i = 0; i < length; i++) {
            if (items.get(i).isSelected()) {
                selected.add(items.get(i));
            }
            if (items.get(i) instanceof IExpandable && ((IExpandable)items.get(i)).getSubItems() != null)
                updateSelectedItemsWithCollapsed(selected, ((IExpandable)items.get(i)).getSubItems());
        }
    }

    /**
     * counts the items in the adapter, respecting subitems regardless of there current visibility
     *
     * @param adapter      the adapter instance
     * @param predicate      predicate against which each item will be checked before counting it
     * @return number of items in the adapter that apply to the predicate
     */
    public static int countItems(final FastItemAdapter adapter, IPredicate predicate) {
        return countItems(adapter.getAdapterItems(), true, false, predicate);
    }

    /**
     * counts the items in the adapter, respecting subitems regardless of there current visibility
     *
     * @param adapter      the adapter instance
     * @param countHeaders      if true, headers will be counted as well
     * @return number of items in the adapter
     */
    public static int countItems(final FastItemAdapter adapter, boolean countHeaders) {
        return countItems(adapter.getAdapterItems(), countHeaders, false, null);
    }

    private static int countItems(List<IItem> items, boolean countHeaders, boolean subItemsOnly, IPredicate predicate) {
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
                    count += countItems(subItems, countHeaders, true, predicate);
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
            else if (!subItemsOnly && getParent(item) == null) {
                if (predicate == null)
                    count++;
                else if (predicate.apply(item))
                    count++;
            }

        }
        return count;
    }

    /**
     * counts the selected items in the adapter underneath an expandable item, recursively
     *
     * @param adapter      the adapter instance
     * @param header      the header who's selected children should be counted
     * @return number of selected items underneath the header
     */
    public static <T extends IItem & IExpandable> int countSelectedSubItems(final FastItemAdapter adapter, T header) {
        Set<IItem> selections = getSelectedItems(adapter);
        return countSelectedSubItems(selections, header);
    }

    public static <T extends IItem & IExpandable> int countSelectedSubItems(Set<IItem> selections, T header) {
        int count = 0;
        List<IItem> subItems = header.getSubItems();
        int items = header.getSubItems() != null ? header.getSubItems().size() : 0;
        for (int i = 0; i < items; i++) {
            if (selections.contains(subItems.get(i)))
                count++;
            if (subItems.get(i) instanceof IExpandable &&  ((IExpandable)subItems.get(i)).getSubItems() != null)
                count += countSelectedSubItems(selections, (T)subItems.get(i));
        }
        return count;
    }

    private static <T extends IExpandable & IItem> T getParent(IItem item) {

        if (item instanceof ISubItem)
            return (T)((ISubItem) item).getParent();
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
        LinkedList<IItem> selectedItems = new LinkedList<>(getSelectedItems(adapter));

        Log.d("DELETE", "selectedItems: " + selectedItems.size());

        // we delete item per item from the adapter directly or from the parent
        // if keepEmptyHeaders is false, we add empty headers to the selected items set via the iterator, so that they are processed in the loop as well
        IItem item, parent;
        int pos, parentPos;
        boolean expanded;
        ListIterator<IItem> it = selectedItems.listIterator();
        while(it.hasNext()){
            item = it.next();
            pos = adapter.getPosition(item);

            // search for parent - if we find one, we remove the item from the parent's subitems directly
            parent = getParent(item);
            if (parent != null) {
                parentPos = adapter.getPosition(parent);
                boolean success = ((IExpandable)parent).getSubItems().remove(item);
                Log.d("DELETE", "success=" + success + " | deletedId=" + item.getIdentifier() + " | parentId=" + parent.getIdentifier() + " (sub items: " + ((IExpandable)parent).getSubItems().size() + ") | parentPos=" + parentPos);

                // check if parent is expanded and notify the adapter about the removed item, if necessary (only if parent is visible)
                if (parentPos != -1 && ((IExpandable)parent).isExpanded())
                    adapter.notifyAdapterSubItemsChanged(parentPos, ((IExpandable)parent).getSubItems().size() + 1);

                // if desired, notify the parent about it's changed items (only if parent is visible!)
                if (parentPos != -1 && notifyParent) {
                    expanded = ((IExpandable)parent).isExpanded();
                    adapter.notifyAdapterItemChanged(parentPos);
                    // expand the item again if it was expanded before calling notifyAdapterItemChanged
                    if (expanded)
                        adapter.expand(parentPos);
                }

                deleted.add(item);

                if (deleteEmptyHeaders && ((IExpandable)parent).getSubItems().size() == 0) {
                    it.add(parent);
                    it.previous();
                }
            } else if (pos != -1){
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

    public interface IPredicate<T>{
        boolean apply(T data);
    }
}