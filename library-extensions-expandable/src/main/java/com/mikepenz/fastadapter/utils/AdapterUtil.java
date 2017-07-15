package com.mikepenz.fastadapter.utils;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IExpandable;
import com.mikepenz.fastadapter.IItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikepenz on 31.12.15.
 */
public class AdapterUtil {
    /**
     * internal method to restore the selection state of subItems
     *
     * @param item          the parent item
     * @param selectedItems the list of selectedItems from the savedInstanceState
     */
    public static <Item extends IItem> void restoreSubItemSelectionStatesForAlternativeStateManagement(Item item, List<String> selectedItems) {
        if (item instanceof IExpandable && !((IExpandable) item).isExpanded() && ((IExpandable) item).getSubItems() != null) {
            List<Item> subItems = (List<Item>) ((IExpandable<Item, ?>) item).getSubItems();
            for (int i = 0, size = subItems.size(); i < size; i++) {
                Item subItem = subItems.get(i);
                String id = String.valueOf(subItem.getIdentifier());
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
            for (int i = 0, size = subItems.size(); i < size; i++) {
                Item subItem = subItems.get(i);
                String id = String.valueOf(subItem.getIdentifier());
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
