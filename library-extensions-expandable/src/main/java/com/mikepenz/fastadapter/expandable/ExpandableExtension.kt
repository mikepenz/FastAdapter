package com.mikepenz.fastadapter.expandable

import android.os.Bundle
import androidx.collection.ArraySet
import androidx.recyclerview.widget.RecyclerView

import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.View

import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.IItemAdapter
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.utils.AdapterPredicate
import com.mikepenz.fastadapter.utils.AdapterUtil

import java.util.ArrayList

/**
 * Created by mikepenz on 04/06/2017.
 */

class ExpandableExtension<Item> :
    IAdapterExtension<Item> where Item : IItem<out RecyclerView.ViewHolder>, Item : ISubItem<*> {

    //
    private var fastAdapter: FastAdapter<Item>? = null
    // only one expanded section
    /**
     * @return if there should be only one expanded, expandable item in the list
     */
    var isOnlyOneExpandedItem = false
        private set

    //-------------------------
    //-------------------------
    //Expandable stuff
    //-------------------------
    //-------------------------

    /**
     * returns the expanded items this contains position and the count of items
     * which are expanded by this position
     *
     * @return the expanded items
     */
    val expanded: SparseIntArray
        get() {
            val expandedItems = SparseIntArray()
            var item: Item?
            var i = 0
            val size = fastAdapter!!.itemCount
            while (i < size) {
                item = fastAdapter!!.getItem(i)
                if (item is IExpandable<*, *> && (item as IExpandable<*, *>).isExpanded) {
                    expandedItems.put(i, (item as IExpandable<*, *>).subItems!!.size)
                }
                i++
            }
            return expandedItems
        }

    /**
     * @return a set with the global positions of all expanded items
     */
    val expandedItems: IntArray
        get() {
            val expandedItems: IntArray
            val expandedItemsList = ArrayList<Int>()
            var item: Item?
            run {
                var i = 0
                val size = fastAdapter!!.itemCount
                while (i < size) {
                    item = fastAdapter!!.getItem(i)
                    if (item is IExpandable<*, *> && (item as IExpandable<*, *>).isExpanded) {
                        expandedItemsList.add(i)
                    }
                    i++
                }
            }

            val expandedItemsListLength = expandedItemsList.size
            expandedItems = IntArray(expandedItemsListLength)
            for (i in 0 until expandedItemsListLength) {
                expandedItems[i] = expandedItemsList[i]
            }
            return expandedItems
        }

    override fun init(fastAdapter: FastAdapter<Item>): ExpandableExtension<Item> {
        this.fastAdapter = fastAdapter
        return this
    }

    /**
     * set if there should only be one opened expandable item
     * DEFAULT: false
     *
     * @param mOnlyOneExpandedItem true if there should be only one expanded, expandable item in the list
     * @return this
     */
    fun withOnlyOneExpandedItem(mOnlyOneExpandedItem: Boolean): ExpandableExtension<Item> {
        this.isOnlyOneExpandedItem = mOnlyOneExpandedItem
        return this
    }

    override fun withSavedInstanceState(savedInstanceState: Bundle?, prefix: String) {
        if (savedInstanceState == null) {
            return
        }
        val expandedItems = savedInstanceState.getStringArrayList(BUNDLE_EXPANDED + prefix)
        var id: String
        var i = 0
        var size = fastAdapter!!.itemCount
        while (i < size) {
            val item = fastAdapter!!.getItem(i)
            id = item!!.identifier.toString()
            if (expandedItems != null && expandedItems.contains(id)) {
                expand(i)
                size = fastAdapter!!.itemCount
            }
            i++
        }
    }

    override fun saveInstanceState(savedInstanceState: Bundle?, prefix: String) {
        if (savedInstanceState == null) {
            return
        }
        val expandedItems = ArrayList<String>()

        var item: Item?
        var i = 0
        val size = fastAdapter!!.itemCount
        while (i < size) {
            item = fastAdapter!!.getItem(i)
            if (item is IExpandable<*, *> && (item as IExpandable<*, *>).isExpanded) {
                expandedItems.add(item.identifier.toString())
            }
            i++
        }
        //remember the collapsed states
        savedInstanceState.putStringArrayList(BUNDLE_EXPANDED + prefix, expandedItems)
    }

    override fun onClick(v: View, pos: Int, fastAdapter: FastAdapter<Item>, item: Item): Boolean {
        val consumed = false
        //if this is a expandable item :D (this has to happen after we handled the selection as we refer to the position)
        if (!consumed && item is IExpandable<*, *>) {
            if ((item as IExpandable<*, *>).isAutoExpanding && (item as IExpandable<*, *>).subItems != null) {
                toggleExpandable(pos)
            }
        }

        //if there should be only one expanded item we want to collapse all the others but the current one (this has to happen after we handled the selection as we refer to the position)
        if (!consumed && isOnlyOneExpandedItem && item is IExpandable<*, *>) {
            if ((item as IExpandable<*, *>).subItems != null && (item as IExpandable<*, *>).subItems!!.size > 0) {
                val expandedItems = getExpandedItemsSameLevel(pos)
                for (i in expandedItems.indices.reversed()) {
                    if (expandedItems[i] != pos) {
                        collapse(expandedItems[i], true)
                    }
                }
            }
        }
        return consumed
    }

    override fun onLongClick(
        v: View,
        pos: Int,
        fastAdapter: FastAdapter<Item>,
        item: Item
    ): Boolean {
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

    override fun notifyAdapterItemMoved(fromPosition: Int, toPosition: Int) {
        //collapse items we move. just in case :D
        collapse(fromPosition)
        collapse(toPosition)
    }

    override fun notifyAdapterItemRangeChanged(position: Int, itemCount: Int, payload: Any?) {
        for (i in position until position + itemCount) {
            val item = fastAdapter!!.getItem(position)
            if (item is IExpandable<*, *> && (item as IExpandable<*, *>).isExpanded) {
                collapse(position)
            }
        }
    }

    override fun set(items: List<Item>, resetFilter: Boolean) {
        //first collapse all items
        collapse(false)
    }

    override fun performFiltering(constraint: CharSequence?) {
        collapse(false)
    }

    /**
     * notifies the fastAdapter about new / removed items within a sub hierarchy
     * NOTE this currently only works for sub items with only 1 level
     *
     * @param position      the global position of the parent item
     * @param previousCount the previous count of sub items
     * @return the new count of subItems
     */
    fun notifyAdapterSubItemsChanged(position: Int, previousCount: Int): Int {
        val item = fastAdapter?.getItem(position)
        if (item != null && item is IExpandable<*, *>) {
            val expandable = item as? IExpandable<*, *>?
            val adapter = fastAdapter?.getAdapter(position)
            if (adapter != null && adapter is IItemAdapter<*, *>) {
                (adapter as? IItemAdapter<*, *>)?.removeRange(position + 1, previousCount)
                expandable?.subItems?.let { subItems ->
                    (adapter as? IItemAdapter<IItem<out RecyclerView.ViewHolder>, *>)?.add(
                        position + 1,
                        subItems
                    )
                }
            }
            return expandable?.subItems?.size ?: 0
        }
        return 0
    }

    /**
     * @param position the global position of the current item
     * @return a set with the global positions of all expanded items on the same level as the current item
     */
    fun getExpandedItemsSameLevel(position: Int): IntArray {
        val item = fastAdapter!!.getItem(position)
        if (item !is ISubItem<*>) {
            //if it isn't a SubItem, has to be on the root level
            return getExpandedItemsRootLevel(position)
        } else {
            val parent = (item as ISubItem<*>).parent as? IExpandable<*, *>
                    ?: //if it has no parent, has to be on the root level
                    return getExpandedItemsRootLevel(position)

            //if it is a SubItem and has a parent, only return the expanded items on the same level
            val expandedItems: IntArray
            val expandedItemsList = ArrayList<Int>()
            for (subItem in parent.subItems!!) {
                if (subItem is IExpandable<*, *> && (subItem as IExpandable<*, *>).isExpanded && subItem !== item) {
                    expandedItemsList.add(fastAdapter!!.getPosition(subItem as Item))
                }
            }
            val expandedItemsListLength = expandedItemsList.size
            expandedItems = IntArray(expandedItemsListLength)
            for (i in 0 until expandedItemsListLength) {
                expandedItems[i] = expandedItemsList[i]
            }
            return expandedItems
        }
    }

    /**
     * @param position the global position of the current item
     * @return a set with the global positions of all expanded items on the root level
     */
    fun getExpandedItemsRootLevel(position: Int): IntArray {
        val expandedItems: IntArray
        val expandedItemsList = ArraySet<Int>()
        val item = fastAdapter?.getItem(position)
        var i = 0
        val size = fastAdapter?.itemCount ?: 0
        while (i < size) {
            val currItem = fastAdapter!!.getItem(i)
            if (currItem is ISubItem<*>) {
                val parent = (currItem as ISubItem<*>).parent
                if (parent is IExpandable<*, *> && (parent as IExpandable<*, *>).isExpanded) {
                    i += (parent as IExpandable<*, *>).subItems!!.size
                    if (parent !== item)
                        expandedItemsList.add(fastAdapter!!.getPosition((parent as Item?)!!))
                }
            }
            i++
        }

        val expandedItemsListLength = expandedItemsList.size
        expandedItems = IntArray(expandedItemsListLength)
        for (i in 0 until expandedItemsListLength) {
            expandedItems[i] = expandedItemsList.valueAt(i)!!
        }
        return expandedItems
    }

    /**
     * toggles the expanded state of the given expandable item at the given position
     *
     * @param position the global position
     */
    fun toggleExpandable(position: Int) {
        val item = fastAdapter!!.getItem(position)
        if (item is IExpandable<*, *> && (item as IExpandable<*, *>).isExpanded) {
            collapse(position)
        } else {
            expand(position)
        }
    }

    /**
     * collapses all expanded items
     *
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    @JvmOverloads
    fun collapse(notifyItemChanged: Boolean = true) {
        val expandedItems = expandedItems
        for (i in expandedItems.indices.reversed()) {
            collapse(expandedItems[i], notifyItemChanged)
        }
    }

    /**
     * collapses (closes) the given collapsible item at the given position
     *
     * @param position          the global position
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    @JvmOverloads
    fun collapse(position: Int, notifyItemChanged: Boolean = false) {
        val expandedItemsCount = intArrayOf(0)
        fastAdapter!!.recursive(object : AdapterPredicate<Item> {
            internal var allowedParents = ArraySet<IItem<*>>()

            override fun apply(
                lastParentAdapter: IAdapter<Item>,
                lastParentPosition: Int,
                item: Item,
                position: Int
            ): Boolean {
                //we do not care about non visible items
                if (position == -1) {
                    return false
                }

                //this is the entrance parent
                if (allowedParents.size > 0 && item is ISubItem<*>) {
                    // Go on until we hit an item with a parent which was not in our expandable hierarchy
                    val parent = (item as ISubItem<*>).parent
                    if (parent == null || !allowedParents.contains(parent)) {
                        return true
                    }
                }

                if (item is IExpandable<*, *>) {
                    val expandable = item as IExpandable<*, *>
                    if (expandable.isExpanded) {
                        expandable.isExpanded = false

                        if (expandable.subItems != null) {
                            expandedItemsCount[0] += expandable.subItems!!.size
                            allowedParents.add(item)
                        }
                    }
                }

                return false
            }
        }, position, true)

        val adapter = fastAdapter!!.getAdapter(position)
        if (adapter != null && adapter is IItemAdapter<*, *>) {
            (adapter as IItemAdapter<*, *>).removeRange(position + 1, expandedItemsCount[0])
        }

        //we need to notify to get the correct drawable if there is one showing the current state
        if (notifyItemChanged) {
            fastAdapter!!.notifyItemChanged(position)
        }
    }

    /**
     * expands all expandable items
     *
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    @JvmOverloads
    fun expand(notifyItemChanged: Boolean = false) {
        val length = fastAdapter!!.itemCount
        for (i in length - 1 downTo 0) {
            expand(i, notifyItemChanged)
        }
    }


    /**
     * opens the expandable item at the given position
     *
     * @param position          the global position
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    @JvmOverloads
    fun expand(position: Int, notifyItemChanged: Boolean = false) {
        val item = fastAdapter!!.getItem(position)
        if (item != null && item is IExpandable<*, *>) {
            val expandable = item as? IExpandable<*, Item>?
            //if this item is not already expanded and has sub items we go on
            if (expandable != null && !expandable.isExpanded && expandable.subItems?.isNotEmpty() == true) {
                val adapter = fastAdapter?.getAdapter(position)
                if (adapter != null && adapter is IItemAdapter<*, *>) {
                    expandable.subItems?.let { subItems ->
                        (adapter as IItemAdapter<*, Item>).addInternal(
                            position + 1,
                            subItems
                        )
                    }
                }

                //remember that this item is now opened (not collapsed)
                expandable.isExpanded = true

                //we need to notify to get the correct drawable if there is one showing the current state
                if (notifyItemChanged) {
                    fastAdapter?.notifyItemChanged(position)
                }
            }
        }
    }

    /**
     * calculates the count of expandable items before a given position
     *
     * @param from     the global start position you should pass here the count of items of the previous adapters (or 0 if you want to start from the beginning)
     * @param position the global position
     * @return the count of expandable items before a given position
     */
    fun getExpandedItemsCount(from: Int, position: Int): Int {
        var totalAddedItems = 0
        //first we find out how many items were added in total
        //also counting subItems
        var tmp: Item?
        for (i in from until position) {
            tmp = fastAdapter!!.getItem(i)
            if (tmp is IExpandable<*, *>) {
                val tmpExpandable = tmp as IExpandable<*, *>?
                if (tmpExpandable?.subItems != null && tmpExpandable.isExpanded) {
                    totalAddedItems += tmpExpandable.subItems?.size ?: 0
                }
            }
        }
        return totalAddedItems
    }

    /**
     * deselects all selections
     */
    fun deselect() {
        val selectExtension =
            fastAdapter?.getExtension<SelectExtension<Item>>(SelectExtension::class.java)
                    ?: return
        fastAdapter?.let { fastAdapter ->
            for (item in AdapterUtil.getAllItems(fastAdapter)) {
                selectExtension.deselect(item)
            }
            fastAdapter.notifyDataSetChanged()
        }
    }

    /**
     * select all items
     *
     * @param considerSelectableFlag true if the select method should not select an item if its not selectable
     */
    fun select(considerSelectableFlag: Boolean) {
        val selectExtension =
            fastAdapter!!.getExtension<SelectExtension<Item>>(SelectExtension::class.java)
                    ?: return
        fastAdapter?.let { fastAdapter ->
            for (item in AdapterUtil.getAllItems(fastAdapter)) {
                selectExtension.select(item, considerSelectableFlag)
            }
            fastAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        private const val BUNDLE_EXPANDED = "bundle_expanded"
    }
}
/**
 * collapses all expanded items
 */
/**
 * collapses (closes) the given collapsible item at the given position
 *
 * @param position the global position
 */
/**
 * expands all expandable items
 */
/**
 * opens the expandable item at the given position
 *
 * @param position the global position
 */
