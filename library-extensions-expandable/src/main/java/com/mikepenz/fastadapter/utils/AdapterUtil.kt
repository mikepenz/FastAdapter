package com.mikepenz.fastadapter.utils

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IExpandable
import java.util.*

/**
 * Created by mikepenz on 31.12.15.
 */
object AdapterUtil {
    /**
     * internal method to restore the selection state of subItems
     *
     * @param item          the parent item
     * @param selectedItems the list of selectedItems from the savedInstanceState
     */
    fun <Item> restoreSubItemSelectionStatesForAlternativeStateManagement(item: Item, selectedItems: List<String>?) where Item : GenericItem, Item : IExpandable<*> {
        if (!item.isExpanded) {
            val subItems = (item as IExpandable<*>).subItems
            var i = 0
            val size = subItems.size
            while (i < size) {
                val subItem = subItems[i] as Item
                val id = subItem.identifier.toString()
                if (selectedItems != null && selectedItems.contains(id)) {
                    subItem.isSelected = true
                }
                restoreSubItemSelectionStatesForAlternativeStateManagement(subItem, selectedItems)
                i++
            }
        }
    }

    /**
     * internal method to find all selections from subItems and sub sub items so we can save those inside our savedInstanceState
     *
     * @param item       the parent item
     * @param selections the ArrayList which will be stored in the savedInstanceState
     */
    fun <Item> findSubItemSelections(item: Item, selections: MutableList<String>) where Item : GenericItem, Item : IExpandable<*> {
        if (!item.isExpanded) {
            val subItems = (item as IExpandable<*>).subItems
            var i = 0
            val size = subItems.size
            while (i < size) {
                val subItem = subItems[i] as Item
                val id = subItem.identifier.toString()
                if (subItem.isSelected) {
                    selections.add(id)
                }
                findSubItemSelections(subItem, selections)
                i++
            }
        }
    }

    /**
     * Gets all items (including sub items) from the FastAdapter
     *
     * @param fastAdapter the FastAdapter
     * @return a list of all items including the whole subItem hirachy
     */
    fun <Item> getAllItems(fastAdapter: FastAdapter<Item>): List<Item> where Item : GenericItem, Item : IExpandable<*> {
        val size = fastAdapter.itemCount
        val items = ArrayList<Item>(size)
        for (i in 0 until size) {
            fastAdapter.getItem(i)?.let {
                items.add(it)
                addAllSubItems(it, items)
            }
        }
        return items
    }

    /**
     * Gets all subItems from a given parent item
     *
     * @param item  the parent from which we add all items
     * @param items the list in which we add the subItems
     */
    fun <Item> addAllSubItems(item: Item?, items: MutableList<Item>) where Item : GenericItem, Item : IExpandable<*> {
        if (item is IExpandable<*> && !item.isExpanded) {
            val subItems = (item as IExpandable<*>).subItems
            var subItem: Item
            var i = 0
            val size = subItems.size
            while (i < size) {
                subItem = subItems[i] as Item
                items.add(subItem)
                addAllSubItems(subItem, items)
                i++
            }
        }
    }
}
