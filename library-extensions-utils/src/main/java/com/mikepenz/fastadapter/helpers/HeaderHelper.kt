package com.mikepenz.fastadapter.helpers

import com.mikepenz.fastadapter.adapters.ModelAdapter
import java.util.Collections
import java.util.Comparator

/**
 * Created by mikepenz on 17.08.16.
 */

class HeaderHelper<Item, HeaderItem : Item> {

    /** The ModelAdapter */
    var modelAdapter: ModelAdapter<Item, *>? = null

    /** The function used to determine headers */
    var groupingFunction: GroupingFunction<Item, HeaderItem>

    /** The comparator to use before adding the headers */
    var comparator: Comparator<Item>? = null

    constructor(groupingFunction: GroupingFunction<Item, HeaderItem>) {
        this.groupingFunction = groupingFunction
    }

    constructor(
            modelAdapter: ModelAdapter<Item, *>,
            groupingFunction: GroupingFunction<Item, HeaderItem>
    ) {
        this.modelAdapter = modelAdapter
        this.groupingFunction = groupingFunction
    }

    /**
     * Call this when your list order has changed or was updated, and you have to readd the headres
     *
     * @param items the list which will get the headers added inbetween
     */
    fun apply(items: MutableList<Item>) {
        //If the list is empty avoid sorting and adding headers.
        var size = items.size
        if (size > 0) {
            //sort beforehand
            if (comparator != null) {
                Collections.sort(items, comparator)
            }

            //we have to get the list size each time, as we will add the headers to it
            var i = -1
            while (i < size) {
                val headerItem: HeaderItem? = when (i) {
                    -1 -> groupingFunction.group(null, items[i + 1], i)
                    size - 1 -> groupingFunction.group(items[i], null, i)
                    else -> groupingFunction.group(items[i], items[i + 1], i)
                }

                if (headerItem != null) {
                    items.add(i + 1, headerItem)
                    i++
                    size++
                }
                i++
            }
        }

        // Set the sorted list to the modelAdapter if provided
        modelAdapter?.set(items)
    }

    interface GroupingFunction<Item, HeaderItem> {
        /**
         * @param currentItem     the current item we check
         * @param nextItem        the item comming after the current item
         * @param currentPosition the current position of the currentItem
         * @return the HeaderItem we want to add after the currentItem
         */
        fun group(currentItem: Item?, nextItem: Item?, currentPosition: Int): HeaderItem
    }
}
