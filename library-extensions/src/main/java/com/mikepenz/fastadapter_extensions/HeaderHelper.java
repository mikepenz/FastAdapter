package com.mikepenz.fastadapter_extensions;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by mikepenz on 17.08.16.
 */

public class HeaderHelper<Item extends IItem> {
    private ItemAdapter<Item> itemAdapter;
    private GroupingFunction<Item> groupingFunction;
    private Comparator<Item> comparator;

    /**
     * @param groupingFunction
     */
    public HeaderHelper(GroupingFunction groupingFunction) {
        this.groupingFunction = groupingFunction;
    }

    /**
     * @param itemAdapter
     * @param groupingFunction
     */
    public HeaderHelper(ItemAdapter<Item> itemAdapter, GroupingFunction<Item> groupingFunction) {
        this.itemAdapter = itemAdapter;
        this.groupingFunction = groupingFunction;
    }

    /**
     * call this when your list order has changed or was updated, and you have to readd the headres
     *
     * @param items the list which will get the headers added inbetween
     */
    public void apply(List<Item> items) {
        //sort beforehand
        if (comparator != null) {
            Collections.sort(items, comparator);
        }

        //we have to get the list size each time, as we will add the headers to it
        for (int i = -1; i < items.size(); i++) {
            Item headerItem;
            if (i == -1) {
                headerItem = groupingFunction.group(null, items.get(i + 1), i);
            } else if (i == items.size() - 1) {
                headerItem = groupingFunction.group(items.get(i), null, i);
            } else {
                headerItem = groupingFunction.group(items.get(i), items.get(i + 1), i);
            }

            if (headerItem != null) {
                items.add(i + 1, headerItem);
                i = i + 1;
            }
        }

        /**
         * set the sorted list to the itemAdapter if provided
         */
        if (itemAdapter != null) {
            itemAdapter.set(items);
        }
    }

    /**
     * @return the comparator to use before adding the headers
     */
    public Comparator<Item> getComparator() {
        return comparator;
    }

    /**
     * @param comparator the comparator to use before adding the headers
     */
    public void setComparator(Comparator<Item> comparator) {
        this.comparator = comparator;
    }

    /**
     * @return the ItemAdapter
     */
    public ItemAdapter<Item> getItemAdapter() {
        return itemAdapter;
    }

    /**
     * @param itemAdapter the ItemAdapter
     */
    public void setItemAdapter(ItemAdapter<Item> itemAdapter) {
        this.itemAdapter = itemAdapter;
    }

    /**
     * @return the function used to determine headers
     */
    public GroupingFunction<Item> getGroupingFunction() {
        return groupingFunction;
    }

    /**
     * @param groupingFunction the function used to determine headers
     */
    public void setGroupingFunction(GroupingFunction<Item> groupingFunction) {
        this.groupingFunction = groupingFunction;
    }


    public interface GroupingFunction<Item extends IItem> {
        /**
         * @param currentItem     the current item we check
         * @param nextItem        the item comming after the current item
         * @param currentPosition the current position of the currentItem
         * @return the HeaderItem we want to add after the currentItem
         */
        Item group(Item currentItem, Item nextItem, int currentPosition);
    }
}
