package com.mikepenz.fastadapter.select

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.collection.ArraySet
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItemAdapter
import com.mikepenz.fastadapter.ISelectionListener
import com.mikepenz.fastadapter.dsl.FastAdapterDsl
import com.mikepenz.fastadapter.extensions.ExtensionsFactories
import com.mikepenz.fastadapter.utils.AdapterPredicate
import java.util.ArrayList

/**
 * Extension method to retrieve or create the SelectExtension from the current FastAdapter.
 * This will return a non null variant and fail if something terrible happens.
 */
fun <Item : GenericItem> FastAdapter<Item>.getSelectExtension(): SelectExtension<Item> {
    SelectExtension.toString() // enforces the vm to lead in the companion object
    return requireOrCreateExtension()
}

/**
 * Extension method to retrieve or create the SelectExtension from the current FastAdapter.
 * This will return a non null variant and fail if something terrible happens.
 */
inline fun <Item : GenericItem> FastAdapter<Item>.selectExtension(block: SelectExtension<Item>.() -> Unit) {
    getSelectExtension().apply(block)
}

/**
 * Created by mikepenz on 04/06/2017.
 */
@FastAdapterDsl
class SelectExtension<Item : GenericItem>(private val fastAdapter: FastAdapter<Item>) : IAdapterExtension<Item> {

    // if enabled we will select the item via a notifyItemChanged -> will animate with the Animator
    // you can also use this if you have any custom logic for selections, and do not depend on the "selected" state of the view
    // note if enabled it will feel a bit slower because it will animate the selection
    /**
     * Select between the different selection behaviors.
     * there are now 2 different variants of selection. you can toggle this via `withSelectWithItemUpdate(boolean)` (where false == default - variant 1)
     * 1.) direct selection via the view "selected" state, we also make sure we do not animate here so no notifyItemChanged is called if we repeatly press the same item
     * 2.) we select the items via a notifyItemChanged. this will allow custom selected logics within your views (isSelected() - do something...) and it will also animate the change via the provided itemAnimator. because of the animation of the itemAnimator the selection will have a small delay (time of animating)
     */
    var selectWithItemUpdate = false
    // if we want multiSelect enabled
    // Enable this if you want multiSelection possible in the list
    var multiSelect = false
    // if we want the multiSelect only on longClick
    // Disable this if you want the selection on a single tap
    var selectOnLongClick = false
    // if a user can deselect a selection via click. required if there is always one selected item!
    // If false, a user can't deselect an item via click (you can still do this programmatically)
    var allowDeselection = true
    // if items are selectable in general
    /** If items are selectable */
    var isSelectable = false

    //a listener that get's notified whenever an item is selected or deselected
    var selectionListener: ISelectionListener<Item>? = null

    //-------------------------
    //-------------------------
    //Selection stuff
    //-------------------------
    //-------------------------

    /**
     * @return a set with the global positions of all selected items (which are currently in the list (includes expanded expandable items))
     */
    val selections: Set<Int>
        get() {
            return (0 until fastAdapter.itemCount).mapNotNullTo(ArraySet<Int>()) { i ->
                i.takeIf { fastAdapter.getItem(i)?.isSelected == true }
            }
        }

    /**
     * @return a set with all items which are currently selected (includes subitems)
     */
    val selectedItems: MutableSet<Item>
        get() {
            val items = ArraySet<Item>()
            fastAdapter.recursive(object : AdapterPredicate<Item> {
                override fun apply(
                        lastParentAdapter: IAdapter<Item>,
                        lastParentPosition: Int,
                        item: Item,
                        position: Int
                ): Boolean {
                    if (item.isSelected) {
                        items.add(item)
                    }
                    return false
                }
            }, false)
            return items
        }

    override fun withSavedInstanceState(savedInstanceState: Bundle?, prefix: String) {
        val selectedItems = savedInstanceState?.getLongArray(BUNDLE_SELECTIONS + prefix) ?: return
        for (id in selectedItems) {
            selectByIdentifier(id, false, true)
        }
    }

    override fun saveInstanceState(savedInstanceState: Bundle?, prefix: String) {
        if (savedInstanceState == null) {
            return
        }

        val selections = selectedItems
        val selectionsArray = LongArray(selections.size)
        for ((i, item) in selections.withIndex()) {
            selectionsArray[i] = item.identifier
        }
        //remember the selections
        savedInstanceState.putLongArray(BUNDLE_SELECTIONS + prefix, selectionsArray)
    }

    override fun onClick(v: View, pos: Int, fastAdapter: FastAdapter<Item>, item: Item): Boolean {
        //handle the selection if the event was not yet consumed, and we are allowed to select an item (only occurs when we select with long click only)
        //this has to happen before expand or collapse. otherwise the position is wrong which is used to select
        if (!selectOnLongClick && isSelectable) {
            handleSelection(v, item, pos)
        }
        return false
    }

    override fun onLongClick(
            v: View,
            pos: Int,
            fastAdapter: FastAdapter<Item>,
            item: Item
    ): Boolean {
        //now handle the selection if we are in multiSelect mode and allow selecting on longClick
        if (selectOnLongClick && isSelectable) {
            handleSelection(v, item, pos)
        }
        return false
    }

    override fun onTouch(
            v: View,
            event: MotionEvent,
            position: Int,
            fastAdapter: FastAdapter<Item>,
            item: Item
    ): Boolean {
        return false
    }

    override fun notifyAdapterDataSetChanged() {}

    override fun notifyAdapterItemRangeInserted(position: Int, itemCount: Int) {}

    override fun notifyAdapterItemRangeRemoved(position: Int, itemCount: Int) {}

    override fun notifyAdapterItemMoved(fromPosition: Int, toPosition: Int) {}

    override fun notifyAdapterItemRangeChanged(position: Int, itemCount: Int, payload: Any?) {}

    override fun set(items: List<Item>, resetFilter: Boolean) {}

    override fun performFiltering(constraint: CharSequence?) {}

    /**
     * Toggles the selection of the item at the given position
     *
     * @param position the global position
     */
    fun toggleSelection(position: Int) {
        if (fastAdapter.getItem(position)?.isSelected == true) {
            deselect(position)
        } else {
            select(position)
        }
    }

    /**
     * Handles the selection and deselects item if multiSelect is disabled
     *
     * @param position the global position
     */
    private fun handleSelection(view: View?, item: Item, position: Int) {
        //if this item is not selectable don't continue
        if (!item.isSelectable) {
            return
        }

        //if we have disabled deselection via click don't continue
        if (item.isSelected && !allowDeselection) {
            return
        }

        val selected = item.isSelected

        if (selectWithItemUpdate || view == null) {
            if (!multiSelect) {
                deselect()
            }
            if (selected) {
                deselect(position)
            } else {
                select(position)
            }
        } else {
            if (!multiSelect) {
                //we have to separately handle deselection here because if we toggle the current item we do not want to deselect this first!
                val selections = selectedItems
                selections.remove(item)
                deselectByItems(selections)
            }

            //we toggle the state of the view
            item.isSelected = !selected
            view.isSelected = !selected

            //notify that the selection changed
            selectionListener?.onSelectionChanged(item, !selected)
        }
    }

    /**
     * Select all items
     *
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    @JvmOverloads
    fun select(considerSelectableFlag: Boolean = false) {
        fastAdapter.recursive(object : AdapterPredicate<Item> {
            override fun apply(
                    lastParentAdapter: IAdapter<Item>,
                    lastParentPosition: Int,
                    item: Item,
                    position: Int
            ): Boolean {
                select(lastParentAdapter, item, RecyclerView.NO_POSITION, false, considerSelectableFlag)
                return false
            }
        }, false)
        fastAdapter.notifyDataSetChanged()
    }

    /**
     * Select's a provided item, this won't notify the adapter
     *
     * @param item                   the item to select
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    fun select(item: Item, considerSelectableFlag: Boolean) {
        if (considerSelectableFlag && !item.isSelectable) {
            return
        }
        item.isSelected = true
        selectionListener?.onSelectionChanged(item, true)
    }

    /**
     * Selects all items at the positions in the iteratable
     *
     * @param positions the global positions to select
     */
    fun select(positions: Iterable<Int>) {
        positions.forEach { select(it) }
    }

    /**
     * Selects an item and remembers its position in the selections list
     *
     * @param position               the global position
     * @param fireEvent              true if the onClick listener should be called
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    @JvmOverloads
    fun select(position: Int, fireEvent: Boolean = false, considerSelectableFlag: Boolean = false) {
        val relativeInfo = fastAdapter.getRelativeInfo(position)
        relativeInfo.item?.let { item ->
            relativeInfo.adapter?.let { adapter ->
                select(adapter, item, position, fireEvent, considerSelectableFlag)
            }
        }
    }

    /**
     * Selects an item and remembers its position in the selections list
     *
     * @param adapter                adapter holding this item (or it's parent)
     * @param item                   the item to select
     * @param position               the global position (or < 0 if the item is not displayed)
     * @param fireEvent              true if the onClick listener should be called
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    fun select(adapter: IAdapter<Item>, item: Item, position: Int, fireEvent: Boolean, considerSelectableFlag: Boolean) {
        if (considerSelectableFlag && !item.isSelectable) {
            return
        }

        item.isSelected = true

        fastAdapter.notifyItemChanged(position)

        selectionListener?.onSelectionChanged(item, true)

        if (fireEvent) {
            fastAdapter.onClickListener?.invoke(null, adapter, item, position)
        }
    }

    /**
     * Selects an item by it's identifier
     *
     * @param identifier             the identifier of the item to select
     * @param fireEvent              true if the onClick listener should be called
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    fun selectByIdentifier(identifier: Long, fireEvent: Boolean, considerSelectableFlag: Boolean) {
        fastAdapter.recursive(object : AdapterPredicate<Item> {
            override fun apply(lastParentAdapter: IAdapter<Item>, lastParentPosition: Int, item: Item, position: Int): Boolean {
                if (item.identifier == identifier) {
                    select(lastParentAdapter, item, position, fireEvent, considerSelectableFlag)
                    return true
                }
                return false
            }
        }, true)
    }

    /**
     * @param identifiers            the set of identifiers to select
     * @param fireEvent              true if the onClick listener should be called
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    fun selectByIdentifiers(identifiers: Set<Long>, fireEvent: Boolean, considerSelectableFlag: Boolean) {
        fastAdapter.recursive(object : AdapterPredicate<Item> {
            override fun apply(lastParentAdapter: IAdapter<Item>, lastParentPosition: Int, item: Item, position: Int): Boolean {
                if (identifiers.contains(item.identifier)) {
                    select(lastParentAdapter, item, position, fireEvent, considerSelectableFlag)
                }
                return false
            }
        }, false)
    }

    /**
     * Deselects all selections
     */
    fun deselect() {
        fastAdapter.recursive(object : AdapterPredicate<Item> {
            override fun apply(lastParentAdapter: IAdapter<Item>, lastParentPosition: Int, item: Item, position: Int): Boolean {
                deselect(item)
                return false
            }
        }, false)
        fastAdapter.notifyDataSetChanged()
    }

    /**
     * Deselects all items at the positions in the iteratable
     *
     * @param positions the global positions to deselect
     */
    fun deselect(positions: MutableIterable<Int>) {
        val entries = positions.iterator()
        while (entries.hasNext()) {
            deselect(entries.next(), entries)
        }
    }

    /**
     * Deselects an item and removes its position in the selections list
     * also takes an iterator to remove items from the map
     *
     * @param position the global position
     * @param entries  the iterator which is used to deselect all
     */
    @JvmOverloads
    fun deselect(position: Int, entries: MutableIterator<Int>? = null) {
        val item = fastAdapter.getItem(position) ?: return
        deselect(item, position, entries)
    }

    /**
     * Deselects an item and removes its position in the selections list
     * also takes an iterator to remove items from the map
     *
     * @param item     the item to deselected
     * @param position the global position (or < 0 if the item is not displayed)
     * @param entries  the iterator which is used to deselect all
     */

    @JvmOverloads
    fun deselect(item: Item, position: Int = RecyclerView.NO_POSITION, entries: MutableIterator<Int>? = null) {
        item.isSelected = false
        entries?.remove()
        if (position >= 0) {
            fastAdapter.notifyItemChanged(position)
        }
        selectionListener?.onSelectionChanged(item, false)
    }


    /**
     * Selects an item by it's identifier
     *
     * @param identifier the identifier of the item to deselect
     */
    fun deselectByIdentifier(identifier: Long) {
        fastAdapter.recursive(object : AdapterPredicate<Item> {
            override fun apply(lastParentAdapter: IAdapter<Item>, lastParentPosition: Int, item: Item, position: Int): Boolean {
                if (item.identifier == identifier) {
                    deselect(item, position, null)
                    return true
                }
                return false
            }
        }, true)
    }

    /**
     * @param identifiers the set of identifiers to deselect
     */
    fun deselectByIdentifiers(identifiers: Set<Long>) {
        fastAdapter.recursive(object : AdapterPredicate<Item> {
            override fun apply(lastParentAdapter: IAdapter<Item>, lastParentPosition: Int, item: Item, position: Int): Boolean {
                if (identifiers.contains(item.identifier)) {
                    deselect(item, position, null)
                }
                return false
            }
        }, false)
    }

    /**
     * @param items the set of items to deselect. They require a identifier.
     */
    fun deselectByItems(items: Set<Item>) {
        fastAdapter.recursive(object : AdapterPredicate<Item> {
            override fun apply(lastParentAdapter: IAdapter<Item>, lastParentPosition: Int, item: Item, position: Int): Boolean {
                if (items.contains(item)) {
                    deselect(item, position, null)
                }
                return false
            }
        }, false)
    }

    /**
     * Deletes all current selected items
     *
     * @return a list of the IItem elements which were deleted
     */
    fun deleteAllSelectedItems(): List<Item> {
        val deletedItems = ArrayList<Item>()

        val positions = ArrayList<Int>()
        fastAdapter.recursive(object : AdapterPredicate<Item> {
            override fun apply(lastParentAdapter: IAdapter<Item>, lastParentPosition: Int, item: Item, position: Int): Boolean {
                if (item.isSelected) {
                    //if it's a subitem remove it from the parent
                    (item as? IExpandable<*>?)?.let { expandable ->
                        //a sub item which is not in the list can be instantly deleted
                        expandable.parent?.subItems?.remove(item)
                    }
                    if (position != RecyclerView.NO_POSITION) {
                        //a normal displayed item can only be deleted afterwards
                        positions.add(position)
                    }
                }
                return false
            }
        }, false)

        //we have to re-fetch the selections array again and again as the position will change after one item is deleted
        for (i in positions.indices.reversed()) {
            val ri = fastAdapter.getRelativeInfo(positions[i])
            if (ri.item != null && ri.item!!.isSelected) { //double verify
                (ri.adapter as? IItemAdapter<*, *>)?.remove(positions[i])
            }
        }
        return deletedItems
    }

    companion object {
        private const val BUNDLE_SELECTIONS = "bundle_selections"

        init {
            ExtensionsFactories.register(SelectExtensionFactory())
        }
    }
}
