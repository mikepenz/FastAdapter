package com.mikepenz.fastadapter_extensions;

import com.mikepenz.fastadapter.adapters.ModelAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by mikepenz on 17.08.16.
 */

public class HeaderHelper<Item, HeaderItem> {
    private ModelAdapter modelAdapter;
    private GroupingFunction<Item, HeaderItem> groupingFunction;
    private Comparator<Item> comparator;

    /**
     * @param groupingFunction
     */
    public HeaderHelper(GroupingFunction groupingFunction) {
        this.groupingFunction = groupingFunction;
    }

    /**
     * @param modelAdapter
     * @param groupingFunction
     */
    public HeaderHelper(ModelAdapter modelAdapter, GroupingFunction<Item, HeaderItem> groupingFunction) {
        this.modelAdapter = modelAdapter;
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
         * set the sorted list to the modelAdapter if provided
         */
        if (modelAdapter != null) {
            modelAdapter.set(items);
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
     * @return the ModelAdapter
     */
    public ModelAdapter getModelAdapter() {
        return modelAdapter;
    }

    /**
     * @param modelAdapter the ModelAdapter
     */
    public void setModelAdapter(ModelAdapter modelAdapter) {
        this.modelAdapter = modelAdapter;
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
