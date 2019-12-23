package com.mikepenz.fastadapter.utils

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.IItemAdapter
import com.mikepenz.fastadapter.expandable.ExpandableExtension
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import java.util.ArrayList
import java.util.LinkedList


/**
 * Created by flisar on 15.09.2016.
 */
object SubItemUtil {
    /**
     * Counts the items in the adapter, respecting subitems regardless of there current visibility
     *
     * @param adapter   the adapter instance
     * @param predicate predicate against which each item will be checked before counting it
     * @return number of items in the adapter that apply to the predicate
     */
    @JvmStatic fun countItems(adapter: IItemAdapter<*, *>, predicate: IPredicate<IItem<*>>): Int {
        return countItems(
                items = adapter.adapterItems,
                countHeaders = true,
                subItemsOnly = false,
                predicate = predicate
        )
    }

    /**
     * Counts the items in the adapter, respecting subitems regardless of there current visibility
     *
     * @param adapter      the adapter instance
     * @param countHeaders if true, headers will be counted as well
     * @return number of items in the adapter
     */
    @JvmStatic fun countItems(adapter: IItemAdapter<*, *>, countHeaders: Boolean): Int {
        return countItems(adapter.adapterItems, countHeaders, false, null)
    }

    @JvmStatic private fun countItems(items: List<IItem<*>>, countHeaders: Boolean, subItemsOnly: Boolean, predicate: IPredicate<IItem<*>>?): Int {
        return getAllItems(items, countHeaders, subItemsOnly, predicate).size
    }

    /**
     * Retrieves a flat list of the items in the adapter, respecting subitems regardless of there current visibility
     *
     * @param adapter   the adapter instance
     * @param predicate predicate against which each item will be checked before adding it to the result
     * @return list of items in the adapter that apply to the predicate
     */
    @JvmStatic fun getAllItems(adapter: IItemAdapter<*, *>, predicate: IPredicate<IItem<*>>): List<IItem<*>> {
        return getAllItems(
                items = adapter.adapterItems,
                countHeaders = true,
                subItemsOnly = false,
                predicate = predicate
        )
    }

    /**
     * Retrieves a flat list of the items in the adapter, respecting subitems regardless of there current visibility
     *
     * @param adapter      the adapter instance
     * @param countHeaders if true, headers will be counted as well
     * @return list of items in the adapter
     */
    @JvmStatic fun getAllItems(adapter: IItemAdapter<*, *>, countHeaders: Boolean): List<IItem<*>> {
        return getAllItems(adapter.adapterItems, countHeaders, false, null)
    }

    /**
     * Retrieves a flat list of the items in the provided list, respecting subitems regardless of there current visibility
     *
     * @param items        the list of items to process
     * @param countHeaders if true, headers will be counted as well
     * @return list of items in the adapter
     */
    @JvmStatic fun getAllItems(items: List<IItem<*>>, countHeaders: Boolean, predicate: IPredicate<IItem<*>>): List<IItem<*>> {
        return getAllItems(items, countHeaders, false, predicate)
    }

    /**
     * Internal function!
     *
     * Why countHeaders and subItems => because the subItemsOnly is an internal flag for the recursive call to optimise it!
     */
    @JvmStatic private fun getAllItems(items: List<IItem<*>>?, countHeaders: Boolean, subItemsOnly: Boolean, predicate: IPredicate<IItem<*>>?): List<IItem<*>> {
        val res = ArrayList<IItem<*>>()
        if (items == null || items.isEmpty()) {
            return res
        }

        val itemCount = items.size
        var item: IItem<*>
        var subItems: List<IItem<*>>?

        for (i in 0 until itemCount) {
            item = items[i]
            if (item is IExpandable<*>) {
                subItems = item.subItems
                if (predicate == null) {
                    if (countHeaders) {
                        res.add(item)
                    }
                    if (subItems.isNotEmpty()) {
                        res.addAll(subItems)
                    }
                    res.addAll(getAllItems(subItems, countHeaders, true, predicate))
                } else {
                    if (countHeaders && predicate.apply(item)) {
                        res.add(item)
                    }
                    for (j in subItems.indices) {
                        subItems[j].let {
                            if (predicate.apply(it)) {
                                res.add(it)
                            }
                        }
                    }
                }
            } else if (!subItemsOnly && getParent<IExpandable<*>>(item) == null) {
                if (predicate?.apply(item) != false) {
                    res.add(item)
                }
            }
            // in some cases, we must manually check, if the item is a sub item, process is optimised as much as possible via the subItemsOnly parameter already
            // sub items will be counted in above if statement!
        }
        return res
    }

    /**
     * Counts the selected items in the adapter underneath an expandable item, recursively
     *
     * @param adapter the adapter instance
     * @param header  the header who's selected children should be counted
     * @return number of selected items underneath the header
     */
    @JvmStatic fun <T> countSelectedSubItems(adapter: FastAdapter<*>, header: T): Int where T : IItem<*>, T : IExpandable<*> {
        val extension = adapter.getExtension(SelectExtension::class.java) as SelectExtension<*>?
        if (extension != null) {
            val selections = extension.selectedItems
            return countSelectedSubItems(selections, header)
        }
        return 0
    }

    @JvmStatic fun <T> countSelectedSubItems(selections: Set<IItem<*>>, header: T): Int where T : IItem<*>, T : IExpandable<*> {
        var count = 0
        val subItems = header.subItems
        for (i in subItems.indices) {
            if (selections.contains(subItems[i])) {
                count++
            }
            if (subItems[i] is IExpandable<*>) {
                count += countSelectedSubItems(selections, subItems[i] as T)
            }
        }
        return count
    }

    /**
     * Select or unselect all sub itmes underneath an expandable item
     *
     * @param adapter the adapter instance
     * @param header  the header who's children should be selected or deselected
     * @param select  the new selected state of the sub items
     */
    @JvmStatic fun <T, Adapter> selectAllSubItems(adapter: Adapter, header: T, select: Boolean) where T : IItem<*>, T : IExpandable<*>, Adapter : FastAdapter<T> {
        selectAllSubItems(adapter, header, select, false, null)
    }

    /**
     * Select or unselect all sub itmes underneath an expandable item
     *
     * @param adapter      the adapter instance
     * @param header       the header who's children should be selected or deselected
     * @param select       the new selected state of the sub items
     * @param notifyParent true, if the parent should be notified about the changes of its children selection state
     * @param payload      payload for the notifying function
     */
    @JvmStatic fun <T, Adapter> selectAllSubItems(adapter: Adapter, header: T, select: Boolean, notifyParent: Boolean, payload: Any?) where T : IItem<*>, Adapter : FastAdapter<T> {
        if (header is IExpandable<*>) {
            val subItems = header.subItems
            val position = adapter.getPosition(header)
            if (header.isExpanded) {
                for (i in subItems.indices) {
                    if ((subItems[i] as IItem<*>).isSelectable) {
                        val extension: SelectExtension<T>? = adapter.getSelectExtension()
                        if (extension != null) {
                            if (select) {
                                extension.select(position + i + 1)
                            } else {
                                extension.deselect(position + i + 1)
                            }
                        }
                    }
                    if (subItems[i] is IExpandable<*>) {
                        selectAllSubItems(adapter, header, select, notifyParent, payload)
                    }
                }
            } else {
                for (i in subItems.indices) {
                    if ((subItems[i] as IItem<*>).isSelectable) {
                        (subItems[i] as IItem<*>).isSelected = select
                    }
                    if (subItems[i] is IExpandable<*>)
                        selectAllSubItems(adapter, header, select, notifyParent, payload)
                }
            }

            // we must notify the view only!
            if (notifyParent && position >= 0) {
                adapter.notifyItemChanged(position, payload)
            }
        }
    }

    @JvmStatic private fun <T> getParent(item: IItem<*>?): T? where T : IExpandable<*>, T : IItem<*> {
        return (item as? IExpandable)?.parent as? T?
    }

    /**
     * Deletes all selected items from the adapter respecting if the are sub items or not
     * subitems are removed from their parents sublists, main items are directly removed
     *
     * Alternatively you might consider also looking at: [SelectExtension.deleteAllSelectedItems]
     *
     * @param deleteEmptyHeaders if true, empty headers will be removed from the adapter
     * @return List of items that have been removed from the adapter
     */
    @JvmStatic fun deleteSelected(fastAdapter: FastAdapter<IItem<*>>, selectExtension: SelectExtension<*>, expandableExtension: ExpandableExtension<*>, notifyParent: Boolean, deleteEmptyHeaders: Boolean): List<IItem<*>> {
        val deleted = ArrayList<IItem<*>>()

        // we use a LinkedList, because this has performance advantages when modifying the listIterator during iteration!
        // Modifying list is O(1)
        val selectedItems = LinkedList(selectExtension.selectedItems)

        // we delete item per item from the adapter directly or from the parent
        // if keepEmptyHeaders is false, we add empty headers to the selected items set via the iterator, so that they are processed in the loop as well
        var item: IItem<*>
        var parent: IItem<*>?
        var pos: Int
        var parentPos: Int
        var expanded: Boolean
        val it = selectedItems.listIterator()
        while (it.hasNext()) {
            item = it.next()
            pos = fastAdapter.getPosition(item)

            // search for parent - if we find one, we remove the item from the parent's subitems directly
            parent = getParent(item)
            if (parent != null) {
                parentPos = fastAdapter.getPosition(parent)
                val subItems = parent.subItems
                subItems.remove(item)
                // check if parent is expanded and notify the adapter about the removed item, if necessary (only if parent is visible)
                if (parentPos != RecyclerView.NO_POSITION && parent.isExpanded) {
                    expandableExtension.notifyAdapterSubItemsChanged(parentPos, subItems.size + 1)
                }

                // if desired, notify the parent about its changed items (only if parent is visible!)
                if (parentPos != RecyclerView.NO_POSITION && notifyParent) {
                    expanded = parent.isExpanded
                    fastAdapter.notifyAdapterItemChanged(parentPos)
                    // expand the item again if it was expanded before calling notifyAdapterItemChanged
                    if (expanded) {
                        expandableExtension.expand(parentPos)
                    }
                }

                deleted.add(item)

                if (deleteEmptyHeaders && subItems.isEmpty()) {
                    it.add(parent)
                    it.previous()
                }
            } else if (pos != RecyclerView.NO_POSITION) {
                // if we did not find a parent, we remove the item from the adapter
                val adapter = fastAdapter.getAdapter(pos)
                if (adapter is IItemAdapter<*, *>) {
                    adapter.remove(pos)
                }
                deleted.add(item)
            }
        }
        return deleted
    }

    /**
     * Deletes all items in identifiersToDelete collection from the adapter respecting if there are sub items or not
     * subitems are removed from their parents sublists, main items are directly removed
     *
     * @param fastAdapter         the adapter to remove the items from
     * @param identifiersToDelete ids of items to remove
     * @param notifyParent        if true, headers of removed items will be notified about the change of their child items
     * @param deleteEmptyHeaders  if true, empty headers will be removed from the adapter
     * @return List of items that have been removed from the adapter
     */
    @JvmStatic fun delete(fastAdapter: FastAdapter<IItem<*>>, expandableExtension: ExpandableExtension<*>, identifiersToDelete: Collection<Long>?, notifyParent: Boolean, deleteEmptyHeaders: Boolean): List<IItem<*>> {
        val deleted = ArrayList<IItem<*>>()
        if (identifiersToDelete == null || identifiersToDelete.isEmpty()) {
            return deleted
        }

        // we use a LinkedList, because this has performance advantages when modifying the listIterator during iteration!
        // Modifying list is O(1)
        val identifiers = LinkedList(identifiersToDelete)

        // we delete item per item from the adapter directly or from the parent
        // if keepEmptyHeaders is false, we add empty headers to the selected items set via the iterator, so that they are processed in the loop as well
        var item: IItem<*>?
        var parent: IItem<*>?
        var pos: Int
        var parentPos: Int
        var expanded: Boolean
        var identifier: Long
        val it = identifiers.listIterator()
        while (it.hasNext()) {
            identifier = it.next()

            pos = fastAdapter.getPosition(identifier)
            item = fastAdapter.getItem(pos) ?: continue

            // search for parent - if we find one, we remove the item from the parent's subitems directly
            parent = getParent(item)
            if (parent != null) {
                parentPos = fastAdapter.getPosition(parent)
                val subItems = parent.subItems
                // check if parent is expanded and notify the adapter about the removed item, if necessary (only if parent is visible)
                if (parentPos != RecyclerView.NO_POSITION && parent.isExpanded) {
                    expandableExtension.notifyAdapterSubItemsChanged(parentPos, subItems.size + 1)
                }

                // if desired, notify the parent about it's changed items (only if parent is visible!)
                if (parentPos != RecyclerView.NO_POSITION && notifyParent) {
                    expanded = parent.isExpanded
                    fastAdapter.notifyAdapterItemChanged(parentPos)
                    // expand the item again if it was expanded before calling notifyAdapterItemChanged
                    if (expanded) {
                        expandableExtension.expand(parentPos)
                    }
                }

                deleted.add(item)

                if (deleteEmptyHeaders && subItems.isEmpty()) {
                    it.add(parent.identifier)
                    it.previous()
                }
            } else if (pos != RecyclerView.NO_POSITION) {
                // if we did not find a parent, we remove the item from the adapter
                val adapter = fastAdapter.getAdapter(pos)
                var success: Boolean
                if (adapter is IItemAdapter<*, *>) {
                    success = true
                    if (success) {
                        fastAdapter.notifyAdapterItemRemoved(pos)
                    }
                }
                deleted.add(item)
            }
        }
        return deleted
    }

    /**
     * Notifies items (incl. sub items if they are currently extended)
     *
     * @param adapter              the adapter
     * @param identifiers          set of identifiers that should be notified
     * @param restoreExpandedState true, if expanded headers should stay expanded
     */
    @JvmStatic
    @JvmOverloads
    fun <Item> notifyItemsChanged(adapter: FastAdapter<IItem<*>>, expandableExtension: ExpandableExtension<*>, identifiers: Set<Long>, restoreExpandedState: Boolean = false) where Item : IItem<*>, Item : IExpandable<*> {
        var item: IItem<*>?
        for (i in 0 until adapter.itemCount) {
            item = adapter.getItem(i)
            if (item is IExpandable<*>) {
                notifyItemsChanged(adapter, expandableExtension, item, identifiers, true, restoreExpandedState)
            } else if (identifiers.contains(item?.identifier)) {
                adapter.notifyAdapterItemChanged(i)
            }
        }
    }

    /**
     * Notifies items (incl. sub items if they are currently extended)
     *
     * @param adapter              the adapter
     * @param header               the expandable header that should be checked (incl. sub items)
     * @param identifiers          set of identifiers that should be notified
     * @param checkSubItems        true, if sub items of headers items should be checked recursively
     * @param restoreExpandedState true, if expanded headers should stay expanded
     */
    @JvmStatic fun <Item> notifyItemsChanged(adapter: FastAdapter<IItem<*>>, expandableExtension: ExpandableExtension<*>, header: Item, identifiers: Set<Long>, checkSubItems: Boolean, restoreExpandedState: Boolean) where Item : IItem<*>, Item : IExpandable<*> {
        val subItems = header.subItems
        val subItemsCount = subItems.size
        val position = adapter.getPosition(header)
        val expanded = header.isExpanded
        // 1) check header itself
        if (identifiers.contains(header.identifier)) {
            adapter.notifyAdapterItemChanged(position)
        }
        // 2) check sub items, recursively
        var item: IItem<*>
        if (header.isExpanded) {
            for (i in 0 until subItemsCount) {
                item = subItems[i]
                if (identifiers.contains(item.identifier)) {
                    //                    Log.d("NOTIFY", "Position=" + position + ", i=" + i);
                    adapter.notifyAdapterItemChanged(position + i + 1)
                }
                if (checkSubItems && item is IExpandable<*>) {
                    notifyItemsChanged(adapter, expandableExtension, item as Item, identifiers, true, restoreExpandedState)
                }
            }
        }
        if (restoreExpandedState && expanded) {
            expandableExtension.expand(position)
        }
    }

    interface IPredicate<T> {
        fun apply(data: T): Boolean
    }
}