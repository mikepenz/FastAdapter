package com.mikepenz.fastadapter.utils;

import android.support.v4.util.ArraySet;
import android.util.SparseIntArray;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by mikepenz on 31.12.15.
 */
public class AdapterUtil {

    /**
     * internal method which correctly set the selected state and expandable state on the newly added items
     *
     * @param fastAdapter   the fastAdapter which manages everything
     * @param startPosition the position of the first item to handle
     * @param endPosition   the position of the last item to handle
     */
    public static <Item extends IItem> void handleStates(FastAdapter<Item> fastAdapter, int startPosition, int endPosition) {
        for (int i = endPosition; i >= startPosition; i--) {
            Item updateItem = fastAdapter.getItem(i);
            if (updateItem != null) {
                if (updateItem.isSelected()) {
                    fastAdapter.getSelections().add(i);
                } else if (fastAdapter.getSelections().contains(i)) {
                    fastAdapter.getSelections().remove(i);
                }
                if (updateItem instanceof IExpandable) {
                    if (((IExpandable) updateItem).isExpanded() && fastAdapter.getExpanded().indexOfKey(i) < 0) {
                        fastAdapter.expand(i);
                    }
                }
            }
        }
    }

    /**
     * internal method to handle the selections if items are added / removed
     *
     * @param positions     the positions map which should be adjusted
     * @param startPosition the global index of the first element modified
     * @param endPosition   the global index up to which the modification changed the indices (should be MAX_INT if we check til the end)
     * @param adjustBy      the value by which the data was shifted
     * @return the adjusted set
     */
    public static Set<Integer> adjustPosition(Set<Integer> positions, int startPosition, int endPosition, int adjustBy) {
        Set<Integer> newPositions = new ArraySet<>();

        for (Integer entry : positions) {
            int position = entry;

            //if our current position is not within the bounds to check for we can add it
            if (position < startPosition || position > endPosition) {
                newPositions.add(position);
            } else if (adjustBy > 0) {
                //if we added items and we are within the bounds we can simply add the adjustBy to our entry
                newPositions.add(position + adjustBy);
            } else if (adjustBy < 0) {
                //if we removed items and we are within the bounds we have to check if the item was removed
                //adjustBy is negative in this case
                if (position > startPosition + adjustBy && position <= startPosition) {
                    //we are within the removed items range we don't add this item anymore
                } else {
                    //otherwise we adjust our position
                    newPositions.add(position + adjustBy);
                }
            }
        }

        return newPositions;
    }

    /**
     * internal method to handle the selections if items are added / removed
     *
     * @param positions     the positions map which should be adjusted
     * @param startPosition the global index of the first element modified
     * @param endPosition   the global index up to which the modification changed the indices (should be MAX_INT if we check til the end)
     * @param adjustBy      the value by which the data was shifted
     * @return the adjusted map
     */
    public static SparseIntArray adjustPosition(SparseIntArray positions, int startPosition, int endPosition, int adjustBy) {
        SparseIntArray newPositions = new SparseIntArray();

        for (int i = 0, size = positions.size(); i < size; i++) {
            int position = positions.keyAt(i);

            //if our current position is not within the bounds to check for we can add it
            if (position < startPosition || position > endPosition) {
                newPositions.put(position, positions.valueAt(i));
            } else if (adjustBy > 0) {
                //if we added items and we are within the bounds we can simply add the adjustBy to our entry
                newPositions.put(position + adjustBy, positions.valueAt(i));
            } else if (adjustBy < 0) {
                //if we removed items and we are within the bounds we have to check if the item was removed
                //adjustBy is negative in this case
                if (position > startPosition + adjustBy && position <= startPosition) {
                    //we are within the removed items range we don't add this item anymore
                } else {
                    //otherwise we adjust our position
                    newPositions.put(position + adjustBy, positions.valueAt(i));
                }
            }
        }

        return newPositions;
    }

    /**
     * internal method to restore the selection state of subItems
     *
     * @param item          the parent item
     * @param selectedItems the list of selectedItems from the savedInstanceState
     */
    public static <Item extends IItem> void restoreSubItemSelectionStatesForAlternativeStateManagement(Item item, List<String> selectedItems) {
        if (item instanceof IExpandable && !((IExpandable) item).isExpanded() && ((IExpandable) item).getSubItems() != null) {
            List<Item> subItems = (List<Item>) ((IExpandable<Item, ?>) item).getSubItems();
            Item subItem;
            String id;
            for (int i = 0, size = subItems.size(); i < size; i++) {
                subItem = subItems.get(i);
                id = String.valueOf(subItem.getIdentifier());
                if (selectedItems != null && selectedItems.contains(id)) {
                    subItem.withSetSelected(true);
                }
                restoreSubItemSelectionStatesForAlternativeStateManagement(subItem, selectedItems);
            }
        }
    }

    /**
     * internal method to find all selections from subItems and sub sub items so we can save those inside our savedInstanceState
     *
     * @param item       the parent item
     * @param selections the ArrayList which will be stored in the savedInstanceState
     */
    public static <Item extends IItem> void findSubItemSelections(Item item, List<String> selections) {
        if (item instanceof IExpandable && !((IExpandable) item).isExpanded() && ((IExpandable) item).getSubItems() != null) {
            List<Item> subItems = (List<Item>) ((IExpandable<Item, ?>) item).getSubItems();
            Item subItem;
            String id;
            for (int i = 0, size = subItems.size(); i < size; i++) {
                subItem = subItems.get(i);
                id = String.valueOf(subItem.getIdentifier());
                if (subItem.isSelected()) {
                    selections.add(id);
                }
                findSubItemSelections(subItem, selections);
            }
        }
    }

    /**
     * Gets all items (including sub items) from the FastAdapter
     *
     * @param fastAdapter the FastAdapter
     * @return a list of all items including the whole subItem hirachy
     */
    public static <Item extends IItem> List<Item> getAllItems(FastAdapter<Item> fastAdapter) {
        int size = fastAdapter.getItemCount();
        List<Item> items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Item item = fastAdapter.getItem(i);
            items.add(item);
            addAllSubItems(item, items);
        }
        return items;
    }

    /**
     * Gets all subItems from a given parent item
     *
     * @param item  the parent from which we add all items
     * @param items the list in which we add the subItems
     */
    public static <Item extends IItem> void addAllSubItems(Item item, List<Item> items) {
        if (item instanceof IExpandable && !((IExpandable) item).isExpanded() && ((IExpandable) item).getSubItems() != null) {
            List<Item> subItems = (List<Item>) ((IExpandable<Item, ?>) item).getSubItems();
            Item subItem;
            for (int i = 0, size = subItems.size(); i < size; i++) {
                subItem = subItems.get(i);
                items.add(subItem);
                addAllSubItems(subItem, items);
            }
        }
    }
}
