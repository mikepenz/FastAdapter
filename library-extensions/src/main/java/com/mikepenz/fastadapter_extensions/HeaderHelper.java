package com.mikepenz.fastadapter_extensions;

import com.mikepenz.fastadapter.adapters.ModelItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by mikepenz on 17.08.16.
 */

public class HeaderHelper<Item, HeaderItem> {
    private ItemAdapter itemAdapter;
    private ModelItemAdapter modelItemAdapter;
    private GroupingFunction<Item, HeaderItem> groupingFunction;
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
    public HeaderHelper(ItemAdapter itemAdapter, GroupingFunction<Item, HeaderItem> groupingFunction) {
        this.itemAdapter = itemAdapter;
        this.groupingFunction = groupingFunction;
    }

    /**
     * @param modelItemAdapter
     * @param groupingFunction
     */
    public HeaderHelper(ModelItemAdapter modelItemAdapter, GroupingFunction<Item, HeaderItem> groupingFunction) {
        this.modelItemAdapter = modelItemAdapter;
        this.groupingFunction = groupingFunction;
    }

    /**
     * call this when your list order has changed or was updated, and you have to readd the headres
     *
     * @param items the list which will get the headers added inbetween
     */
    public void apply(List items) {
        //If the list is empty avoid sorting and adding headers.
        int size = items.size();
        if (size > 0) {
            //sort beforehand
            if (comparator != null) {
                Collections.sort(items, comparator);
            }

            //we have to get the list size each time, as we will add the headers to it
            for (int i = -1; i < size; i++) {
                HeaderItem headerItem;
                if (i == -1) {
                    headerItem = groupingFunction.group(null, (Item) items.get(i + 1), i);
                } else if (i == size - 1) {
                    headerItem = groupingFunction.group((Item) items.get(i), null, i);
                } else {
                    headerItem = groupingFunction.group((Item) items.get(i), (Item) items.get(i + 1), i);
                }

                if (headerItem != null) {
                    items.add(i + 1, headerItem);
                    i = i + 1;
                    size = size + 1;
                }
            }
        }

        /**
         * set the sorted list to the itemAdapter if provided
         */
        if (itemAdapter != null) {
            itemAdapter.set(items);
        }
        if (modelItemAdapter != null) {
            modelItemAdapter.setModel(items);
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
    public ItemAdapter getItemAdapter() {
        return itemAdapter;
    }

    /**
     * @param itemAdapter the ItemAdapter
     */
    public void setItemAdapter(ItemAdapter itemAdapter) {
        this.itemAdapter = itemAdapter;
        this.modelItemAdapter = null;
    }

    /**
     * @return the ModelItemAdapter
     */
    public ModelItemAdapter getModelItemAdapter() {
        return modelItemAdapter;
    }

    /**
     * @param modelItemAdapter the ModelItemAdapter
     */
    public void setModelItemAdapter(ModelItemAdapter modelItemAdapter) {
        this.modelItemAdapter = modelItemAdapter;
        this.itemAdapter = null;
    }

    /**
     * @return the function used to determine headers
     */
    public GroupingFunction<Item, HeaderItem> getGroupingFunction() {
        return groupingFunction;
    }

    /**
     * @param groupingFunction the function used to determine headers
     */
    public void setGroupingFunction(GroupingFunction<Item, HeaderItem> groupingFunction) {
        this.groupingFunction = groupingFunction;
    }


    public interface GroupingFunction<Item, HeaderItem> {
        /**
         * @param currentItem     the current item we check
         * @param nextItem        the item comming after the current item
         * @param currentPosition the current position of the currentItem
         * @return the HeaderItem we want to add after the currentItem
         */
        HeaderItem group(Item currentItem, Item nextItem, int currentPosition);
    }
}
