package com.mikepenz.fastadapter.select

import android.os.Bundle
import androidx.collection.ArraySet
import androidx.recyclerview.widget.RecyclerView

import android.view.MotionEvent
import android.view.View

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.IItemAdapter
import com.mikepenz.fastadapter.ISelectionListener
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.utils.AdapterPredicate

import java.util.ArrayList

/**
 * Created by mikepenz on 04/06/2017.
 */

class SelectExtension<Item : IItem<out RecyclerView.ViewHolder>> : IAdapterExtension<Item> {

    //
    private var mFastAdapter: FastAdapter<Item>? = null
    // if enabled we will select the item via a notifyItemChanged -> will animate with the Animator
    // you can also use this if you have any custom logic for selections, and do not depend on the "selected" state of the view
    // note if enabled it will feel a bit slower because it will animate the selection
    private var mSelectWithItemUpdate = false
    // if we want multiSelect enabled
    private var mMultiSelect = false
    // if we want the multiSelect only on longClick
    private var mSelectOnLongClick = false
    // if a user can deselect a selection via click. required if there is always one selected item!
    private var mAllowDeselection = true
    // if items are selectable in general
    /**
     * @return if items are selectable
     */
    var isSelectable = false
        private set

    //listeners we can attach
    private var mSelectionListener: ISelectionListener<Item>? = null

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
            val selections = ArraySet<Int>()
            var i = 0
            val size = mFastAdapter!!.itemCount
            while (i < size) {
                if (mFastAdapter!!.getItem(i)!!.isSelected) {
                    selections.add(i)
                }
                i++
            }
            return selections
        }

    /**
     * @return a set with all items which are currently selected (includes subitems)
     */
    val selectedItems: MutableSet<Item>
        get() {
            val items = ArraySet<Item>()
            mFastAdapter!!.recursive(object : AdapterPredicate<Item> {
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


    /**
     * select between the different selection behaviors.
     * there are now 2 different variants of selection. you can toggle this via `withSelectWithItemUpdate(boolean)` (where false == default - variant 1)
     * 1.) direct selection via the view "selected" state, we also make sure we do not animate here so no notifyItemChanged is called if we repeatly press the same item
     * 2.) we select the items via a notifyItemChanged. this will allow custom selected logics within your views (isSelected() - do something...) and it will also animate the change via the provided itemAnimator. because of the animation of the itemAnimator the selection will have a small delay (time of animating)
     *
     * @param selectWithItemUpdate true if notifyItemChanged should be called upon select
     * @return this
     */
    fun withSelectWithItemUpdate(selectWithItemUpdate: Boolean): SelectExtension<Item> {
        this.mSelectWithItemUpdate = selectWithItemUpdate
        return this
    }

    /**
     * Enable this if you want multiSelection possible in the list
     *
     * @param multiSelect true to enable multiSelect
     * @return this
     */
    fun withMultiSelect(multiSelect: Boolean): SelectExtension<Item> {
        mMultiSelect = multiSelect
        return this
    }

    /**
     * Disable this if you want the selection on a single tap
     *
     * @param selectOnLongClick false to do select via single click
     * @return this
     */
    fun withSelectOnLongClick(selectOnLongClick: Boolean): SelectExtension<Item> {
        mSelectOnLongClick = selectOnLongClick
        return this
    }

    /**
     * If false, a user can't deselect an item via click (you can still do this programmatically)
     *
     * @param allowDeselection true if a user can deselect an already selected item via click
     * @return this
     */
    fun withAllowDeselection(allowDeselection: Boolean): SelectExtension<Item> {
        this.mAllowDeselection = allowDeselection
        return this
    }

    /**
     * set if no item is selectable
     *
     * @param selectable true if items are selectable
     * @return this
     */
    fun withSelectable(selectable: Boolean): SelectExtension<Item> {
        this.isSelectable = selectable
        return this
    }

    /**
     * set a listener that get's notified whenever an item is selected or deselected
     *
     * @param selectionListener the listener that will be notified about selection changes
     * @return this
     */
    fun withSelectionListener(selectionListener: ISelectionListener<Item>): SelectExtension<Item> {
        this.mSelectionListener = selectionListener
        return this
    }

    override fun init(fastAdapter: FastAdapter<Item>): IAdapterExtension<Item> {
        mFastAdapter = fastAdapter
        return this
    }

    override fun withSavedInstanceState(savedInstanceState: Bundle?, prefix: String) {
        if (savedInstanceState == null) {
            return
        }
        val selectedItems = savedInstanceState.getLongArray(BUNDLE_SELECTIONS + prefix)
        if (selectedItems != null) {
            for (id in selectedItems) {
                selectByIdentifier(id, false, true)
            }
        }
    }

    override fun saveInstanceState(savedInstanceState: Bundle?, prefix: String) {
        if (savedInstanceState == null) {
            return
        }

        val selections = selectedItems
        val selectionsArray = LongArray(selections.size)
        var i = 0
        for (item in selections) {
            selectionsArray[i] = item.identifier
            i++
        }
        //remember the selections
        savedInstanceState.putLongArray(BUNDLE_SELECTIONS + prefix, selectionsArray)
    }

    override fun onClick(v: View, pos: Int, fastAdapter: FastAdapter<Item>, item: Item): Boolean {
        //handle the selection if the event was not yet consumed, and we are allowed to select an item (only occurs when we select with long click only)
        //this has to happen before expand or collapse. otherwise the position is wrong which is used to select
        if (!mSelectOnLongClick && isSelectable) {
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
        if (mSelectOnLongClick && isSelectable) {
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

    override fun set(items: List<Item>, resetFilter: Boolean) {

    }

    override fun performFiltering(constraint: CharSequence?) {

    }

    /**
     * toggles the selection of the item at the given position
     *
     * @param position the global position
     */
    fun toggleSelection(position: Int) {
        if (mFastAdapter!!.getItem(position)!!.isSelected) {
            deselect(position)
        } else {
            select(position)
        }
    }

    /**
     * handles the selection and deselects item if multiSelect is disabled
     *
     * @param position the global position
     */
    private fun handleSelection(view: View?, item: Item, position: Int) {
        //if this item is not selectable don't continue
        if (!item.isSelectable) {
            return
        }

        //if we have disabled deselection via click don't continue
        if (item.isSelected && !mAllowDeselection) {
            return
        }

        val selected = item.isSelected

        if (mSelectWithItemUpdate || view == null) {
            if (!mMultiSelect) {
                deselect()
            }
            if (selected) {
                deselect(position)
            } else {
                select(position)
            }
        } else {
            if (!mMultiSelect) {
                //we have to separately handle deselection here because if we toggle the current item we do not want to deselect this first!
                val selections = selectedItems
                selections.remove(item)
                deselectByItems(selections)
            }

            //we toggle the state of the view
            item.isSelected = !selected
            view.isSelected = !selected

            //notify that the selection changed
            if (mSelectionListener != null)
                mSelectionListener!!.onSelectionChanged(item, !selected)
        }
    }

    /**
     * select all items
     *
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    @JvmOverloads
    fun select(considerSelectableFlag: Boolean = false) {
        mFastAdapter!!.recursive(object : AdapterPredicate<Item> {
            override fun apply(
                lastParentAdapter: IAdapter<Item>,
                lastParentPosition: Int,
                item: Item,
                position: Int
            ): Boolean {
                select(lastParentAdapter, item, -1, false, considerSelectableFlag)
                return false
            }
        }, false)
        mFastAdapter!!.notifyDataSetChanged()
    }

    /**
     * select's a provided item, this won't notify the adapter
     *
     * @param item                   the item to select
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    fun select(item: Item, considerSelectableFlag: Boolean) {
        if (considerSelectableFlag && !item.isSelectable) {
            return
        }
        item.isSelected = true
        if (mSelectionListener != null) {
            mSelectionListener!!.onSelectionChanged(item, true)
        }
    }

    /**
     * selects all items at the positions in the iteratable
     *
     * @param positions the global positions to select
     */
    fun select(positions: Iterable<Int>) {
        for (position in positions) {
            select(position)
        }
    }

    /**
     * selects an item and remembers its position in the selections list
     *
     * @param position               the global position
     * @param fireEvent              true if the onClick listener should be called
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    @JvmOverloads
    fun select(position: Int, fireEvent: Boolean = false, considerSelectableFlag: Boolean = false) {
        val relativeInfo = mFastAdapter!!.getRelativeInfo(position)
        if (relativeInfo == null || relativeInfo.item == null) {
            return
        }
        select(
            relativeInfo.adapter!!,
            relativeInfo.item!!,
            position,
            fireEvent,
            considerSelectableFlag
        )
    }

    /**
     * selects an item and remembers its position in the selections list
     *
     * @param adapter                adapter holding this item (or it's parent)
     * @param item                   the item to select
     * @param position               the global position (or &lt; 0 if the item is not displayed)
     * @param fireEvent              true if the onClick listener should be called
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    fun select(
        adapter: IAdapter<Item>,
        item: Item,
        position: Int,
        fireEvent: Boolean,
        considerSelectableFlag: Boolean
    ) {
        if (considerSelectableFlag && !item.isSelectable) {
            return
        }

        item.isSelected = true

        mFastAdapter!!.notifyItemChanged(position)

        if (mSelectionListener != null)
            mSelectionListener!!.onSelectionChanged(item, true)

        if (mFastAdapter!!.onClickListener != null && fireEvent) {
            mFastAdapter!!.onClickListener!!.onClick(null, adapter, item, position)
        }
    }

    /**
     * selects an item by it's identifier
     *
     * @param identifier             the identifier of the item to select
     * @param fireEvent              true if the onClick listener should be called
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    fun selectByIdentifier(identifier: Long, fireEvent: Boolean, considerSelectableFlag: Boolean) {
        mFastAdapter!!.recursive(object : AdapterPredicate<Item> {
            override fun apply(
                lastParentAdapter: IAdapter<Item>,
                lastParentPosition: Int,
                item: Item,
                position: Int
            ): Boolean {
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
    fun selectByIdentifiers(
        identifiers: Set<Long>,
        fireEvent: Boolean,
        considerSelectableFlag: Boolean
    ) {
        mFastAdapter!!.recursive(object : AdapterPredicate<Item> {
            override fun apply(
                lastParentAdapter: IAdapter<Item>,
                lastParentPosition: Int,
                item: Item,
                position: Int
            ): Boolean {
                if (identifiers.contains(item.identifier)) {
                    select(lastParentAdapter, item, position, fireEvent, considerSelectableFlag)
                }
                return false
            }
        }, false)
    }

    /**
     * deselects all selections
     */
    fun deselect() {
        mFastAdapter!!.recursive(object : AdapterPredicate<Item> {
            override fun apply(
                lastParentAdapter: IAdapter<Item>,
                lastParentPosition: Int,
                item: Item,
                position: Int
            ): Boolean {
                deselect(item)
                return false
            }
        }, false)
        mFastAdapter!!.notifyDataSetChanged()
    }

    /**
     * deselects all items at the positions in the iteratable
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
     * deselects an item and removes its position in the selections list
     * also takes an iterator to remove items from the map
     *
     * @param position the global position
     * @param entries  the iterator which is used to deselect all
     */
    @JvmOverloads
    fun deselect(position: Int, entries: MutableIterator<Int>? = null) {
        val item = mFastAdapter?.getItem(position) ?: return
        deselect(item, position, entries)
    }

    /**
     * deselects an item and removes its position in the selections list
     * also takes an iterator to remove items from the map
     *
     * @param item     the item to deselected
     * @param position the global position (or &lt; 0 if the item is not displayed)
     * @param entries  the iterator which is used to deselect all
     */

    @JvmOverloads
    fun deselect(item: Item, position: Int = -1, entries: MutableIterator<Int>? = null) {
        item.isSelected = false
        entries?.remove()
        if (position >= 0) {
            mFastAdapter!!.notifyItemChanged(position)
        }

        if (mSelectionListener != null) {
            mSelectionListener!!.onSelectionChanged(item, false)
        }
    }


    /**
     * selects an item by it's identifier
     *
     * @param identifier the identifier of the item to deselect
     */
    fun deselectByIdentifier(identifier: Long) {
        mFastAdapter!!.recursive(object : AdapterPredicate<Item> {
            override fun apply(
                lastParentAdapter: IAdapter<Item>,
                lastParentPosition: Int,
                item: Item,
                position: Int
            ): Boolean {
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
        mFastAdapter!!.recursive(object : AdapterPredicate<Item> {
            override fun apply(
                lastParentAdapter: IAdapter<Item>,
                lastParentPosition: Int,
                item: Item,
                position: Int
            ): Boolean {
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
        mFastAdapter!!.recursive(object : AdapterPredicate<Item> {
            override fun apply(
                lastParentAdapter: IAdapter<Item>,
                lastParentPosition: Int,
                item: Item,
                position: Int
            ): Boolean {
                if (items.contains(item)) {
                    deselect(item, position, null)
                }
                return false
            }
        }, false)
    }

    /**
     * deletes all current selected items
     *
     * @return a list of the IItem elements which were deleted
     */
    fun deleteAllSelectedItems(): List<Item> {
        val deletedItems = ArrayList<Item>()

        val positions = ArrayList<Int>()
        mFastAdapter!!.recursive(object : AdapterPredicate<Item> {
            override fun apply(
                lastParentAdapter: IAdapter<Item>,
                lastParentPosition: Int,
                item: Item,
                position: Int
            ): Boolean {
                if (item.isSelected) {
                    //if it's a subitem remove it from the parent
                    if (item is ISubItem<*>) {
                        //a sub item which is not in the list can be instantly deleted
                        val parent = (item as ISubItem<*>).parent as IExpandable<*, *>?
                        //parent should not be null, but check in any case..
                        if (parent != null) {
                            parent.subItems!!.remove(item)
                        }
                    }
                    if (position != -1) {
                        //a normal displayed item can only be deleted afterwards
                        positions.add(position)
                    }
                }
                return false
            }
        }, false)

        //we have to re-fetch the selections array again and again as the position will change after one item is deleted
        for (i in positions.indices.reversed()) {
            val ri = mFastAdapter!!.getRelativeInfo(positions[i])
            if (ri.item != null && ri.item!!.isSelected) { //double verify
                if (ri.adapter != null && ri.adapter is IItemAdapter<*, *>) {
                    (ri.adapter as IItemAdapter<*, *>).remove(positions[i])
                }
            }
        }
        return deletedItems
    }

    companion object {
        protected val BUNDLE_SELECTIONS = "bundle_selections"
    }
}
/**
 * select all items
 */
/**
 * selects an item and remembers its position in the selections list
 *
 * @param position the global position
 */
/**
 * selects an item and remembers its position in the selections list
 *
 * @param position  the global position
 * @param fireEvent true if the onClick listener should be called
 */
/**
 * deselect's a provided item, this won't notify the adapter
 *
 * @param item the item to select
 */
/**
 * deselects an item and removes its position in the selections list
 *
 * @param position the global position
 */
