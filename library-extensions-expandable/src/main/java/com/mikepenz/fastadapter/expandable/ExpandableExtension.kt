package com.mikepenz.fastadapter.expandable

import android.os.Bundle
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.View
import androidx.collection.ArraySet
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IAdapterExtension
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.IItemAdapter
import com.mikepenz.fastadapter.IParentItem
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.dsl.FastAdapterDsl
import com.mikepenz.fastadapter.extensions.ExtensionsFactories
import com.mikepenz.fastadapter.utils.AdapterPredicate

/**
 * Extension method to retrieve or create the ExpandableExtension from the current FastAdapter
 * This will return a non null variant and fail
 */
fun <Item : GenericItem> FastAdapter<Item>.getExpandableExtension(): ExpandableExtension<Item> {
    ExpandableExtension.toString() // enforces the vm to lead in the companion object
    return requireOrCreateExtension()
}

/**
 * Extension method to retrieve or create the ExpandableExtension from the current FastAdapter
 * This will return a non null variant and fail
 */
inline fun <Item : GenericItem> FastAdapter<Item>.expandableExtension(block: ExpandableExtension<Item>.() -> Unit) {
    getExpandableExtension().apply(block)
}

/** Internal helper function to check if an item is expanded. */
internal val IItem<out RecyclerView.ViewHolder>?.isExpanded: Boolean
    get() = (this as? IExpandable<*>)?.isExpanded == true

/** Internal helper function to execute the block if the item is expandable */
internal fun <R> IItem<out RecyclerView.ViewHolder>?.ifExpandable(block: (IExpandable<*>) -> R): R? {
    return (this as? IExpandable<*>)?.let(block)
}

/** Internal helper function to execute the block if the item is expandable */
internal fun <R> IItem<out RecyclerView.ViewHolder>?.ifExpandableParent(block: (IExpandable<*>, IParentItem<*>) -> R): R? {
    return (this as? IExpandable<*>)?.parent?.let {
        block.invoke(this, it)
    }
}

/**
 * Created by mikepenz on 04/06/2017.
 */
@FastAdapterDsl
class ExpandableExtension<Item : GenericItem>(private val fastAdapter: FastAdapter<Item>) :
        IAdapterExtension<Item> {

    private val collapseAdapterPredicate = object : AdapterPredicate<Item> {
        private var allowedParents = ArraySet<IItem<*>>()

        private var expandedItemsCount = 0

        override fun apply(
                lastParentAdapter: IAdapter<Item>,
                lastParentPosition: Int,
                item: Item,
                position: Int
        ): Boolean {
            //we do not care about non visible items
            if (position == RecyclerView.NO_POSITION) {
                return false
            }

            //this is the entrance parent
            if (allowedParents.isNotEmpty()) {
                // Go on until we hit an item with a parent which was not in our expandable hierarchy
                val parent = (item as? ISubItem<*>)?.parent
                if (parent == null || !allowedParents.contains(parent)) {
                    return true
                }
            }

            item.ifExpandable { expandable ->
                if (expandable.isExpanded) {
                    expandable.isExpanded = false

                    expandedItemsCount += expandable.subItems.size
                    allowedParents.add(item)
                }
            }
            return false
        }

        fun collapse(position: Int, fastAdapter: FastAdapter<Item>): Int {
            expandedItemsCount = 0
            allowedParents.clear()
            fastAdapter.recursive(this, position, true)
            return expandedItemsCount
        }
    }

    // only one expanded section
    /** If there should be only one expanded, expandable item in the list */
    var isOnlyOneExpandedItem = false

    //-------------------------
    //-------------------------
    //Expandable stuff
    //-------------------------
    //-------------------------

    /**
     * Returns the expanded items this contains position and the count of items
     * which are expanded by this position
     *
     * @return the expanded items
     */
    val expanded: SparseIntArray
        get() {
            val expandedItems = SparseIntArray()
            for (i in 0 until fastAdapter.itemCount) {
                fastAdapter.getItem(i).ifExpandable { expandableItem ->
                    if (expandableItem.isExpanded) {
                        expandedItems.put(i, expandableItem.subItems.size)
                    }
                }
            }
            return expandedItems
        }

    /**
     * @return a set with the global positions of all expanded items
     */
    val expandedItems: IntArray
        get() = (0 until fastAdapter.itemCount).filter { fastAdapter.getItem(it).isExpanded }.toIntArray()

    override fun withSavedInstanceState(savedInstanceState: Bundle?, prefix: String) {
        val expandedItems = savedInstanceState?.getLongArray(BUNDLE_EXPANDED + prefix) ?: return
        var id: Long?
        var i = 0
        var size = fastAdapter.itemCount
        while (i < size) {
            id = fastAdapter.getItem(i)?.identifier
            if (id != null && expandedItems.contains(id)) {
                expand(i)
                size = fastAdapter.itemCount
            }
            i++
        }
    }

    override fun saveInstanceState(savedInstanceState: Bundle?, prefix: String) {
        if (savedInstanceState == null) {
            return
        }
        val expandedItems = (0 until fastAdapter.itemCount).asSequence()
                .mapNotNull { fastAdapter.getItem(it) }
                .filter { it.isExpanded }
                .map { it.identifier }
                .toList()
        //remember the collapsed states
        savedInstanceState.putLongArray(BUNDLE_EXPANDED + prefix, expandedItems.toLongArray())
    }

    override fun onClick(v: View, pos: Int, fastAdapter: FastAdapter<Item>, item: Item): Boolean {
        //if this is a expandable item :D (this has to happen after we handled the selection as we refer to the position)
        item.ifExpandable { expandableItem ->
            if (expandableItem.isAutoExpanding) {
                toggleExpandable(pos)
            }
            //if there should be only one expanded item we want to collapse all the others but the current one (this has to happen after we handled the selection as we refer to the position)
            if (isOnlyOneExpandedItem) {
                if (expandableItem.subItems.isNotEmpty()) {
                    val expandedItems = getExpandedItemsSameLevel(pos)
                    for (i in expandedItems.indices.reversed()) {
                        if (expandedItems[i] != pos) {
                            collapse(expandedItems[i], true)
                        }
                    }
                }
            }
        }
        return false
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
            if (fastAdapter.getItem(position).isExpanded) {
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
     * Notifies the fastAdapter about new / removed items within a sub hierarchy
     * NOTE this currently only works for sub items with only 1 level
     *
     * @param position      the global position of the parent item
     * @param previousCount the previous count of sub items
     * @return the new count of subItems
     */
    fun notifyAdapterSubItemsChanged(position: Int, previousCount: Int): Int {
        return fastAdapter.getItem(position).ifExpandable { expandable ->
            val adapter = fastAdapter.getAdapter(position)
            if (adapter != null && adapter is IItemAdapter<*, *>) {
                (adapter as? IItemAdapter<*, *>)?.removeRange(position + 1, previousCount)
                expandable.subItems.let { subItems ->
                    (adapter as? IItemAdapter<GenericItem, *>?)?.add(
                            position + 1,
                            subItems
                    )
                }
            }
            expandable.subItems.size
        } ?: 0
    }

    /**
     * @param position the global position of the current item
     * @return a set with the global positions of all expanded items on the same level as the current item
     */
    fun getExpandedItemsSameLevel(position: Int): List<Int> {
        val result = fastAdapter.getItem(position).ifExpandableParent { child, parent ->
            //if it is a SubItem and has a parent, only return the expanded items on the same level
            parent.subItems.asSequence()
                    .filter { it.isExpanded && it !== child }
                    .mapNotNull { it as? Item? }
                    .map { fastAdapter.getPosition(it) }
                    .toList()
        }
        return result ?: getExpandedItemsRootLevel(position)
    }

    /**
     * @param position the global position of the current item
     * @return a set with the global positions of all expanded items on the root level
     */
    fun getExpandedItemsRootLevel(position: Int): List<Int> {
        val expandedItemsList = mutableListOf<Int>()
        val item = fastAdapter.getItem(position)

        var i = 0
        val size = fastAdapter.itemCount
        while (i < size) {
            fastAdapter.getItem(i).ifExpandableParent { _, parent ->
                if (parent.isExpanded) {
                    i += parent.subItems.size
                    if (parent !== item && parent as? Item != null) {
                        expandedItemsList.add(fastAdapter.getPosition(parent))
                    }
                }
            }
            i++
        }
        return expandedItemsList
    }

    /**
     * Toggles the expanded state of the given expandable item at the given position
     *
     * @param position the global position
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    @JvmOverloads
    fun toggleExpandable(position: Int, notifyItemChanged: Boolean = true) {
        val item = fastAdapter.getItem(position) as? IExpandable<*> ?: return
        if (item.isExpanded) {
            collapse(position, notifyItemChanged)
        } else {
            expand(position, notifyItemChanged)
        }
    }

    /**
     * Collapses all expanded items
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
     * Collapses (closes) the given collapsible item at the given position
     *
     * @param position          the global position
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    @JvmOverloads
    fun collapse(position: Int, notifyItemChanged: Boolean = false) {
        val adapter = fastAdapter.getAdapter(position)
        (adapter as? IItemAdapter<*, *>?)?.removeRange(
                position + 1,
                collapseAdapterPredicate.collapse(position, fastAdapter)
        )
        //we need to notify to get the correct drawable if there is one showing the current state
        if (notifyItemChanged) {
            fastAdapter.notifyItemChanged(position)
        }
    }

    /**
     * Collapses (closes) the given collapsible item at the given position with parents who contains this item
     *
     * @param position          the global position
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    @JvmOverloads
    fun collapseIncludeParents(position: Int, notifyItemChanged: Boolean = false) {
        val parents = getExpandableParents(position)

        parents.forEach { collapse(fastAdapter.getPosition(it.identifier)) }

        //we need to notify to get the correct drawable if there is one showing the current state
        if (notifyItemChanged) {
            fastAdapter.notifyItemChanged(position)
        }
    }

    /**
     * Expands all expandable items
     *
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    @JvmOverloads
    fun expand(notifyItemChanged: Boolean = false) {
        val length = fastAdapter.itemCount
        for (i in length - 1 downTo 0) {
            expand(i, notifyItemChanged)
        }
    }


    /**
     * Opens the expandable item at the given position
     *
     * @param position          the global position
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    @JvmOverloads
    fun expand(position: Int, notifyItemChanged: Boolean = false) {
        val expandable = fastAdapter.getItem(position) as? IExpandable<*> ?: return
        //if this item is not already expanded and has sub items we go on
        if (!expandable.isExpanded && expandable.subItems.isNotEmpty()) {
            val adapter = fastAdapter.getAdapter(position)
            if (adapter != null && adapter is IItemAdapter<*, *>) {
                (expandable.subItems as? List<Item>?)?.let { subItems ->
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
                fastAdapter.notifyItemChanged(position)
            }
        }
    }

    /**
     * Expand all items on a path from the root to a certain item
     *
     * @param item              item to be expanded
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    @JvmOverloads
    fun expandAllOnPath(item: IExpandable<*>?, notifyItemChanged: Boolean = false) {
        val parents = getExpandableParents(item ?: return)

        parents.forEach { expand(fastAdapter.getPosition(it.identifier)) }

        //we need to notify to get the correct drawable if there is one showing the current state
        if (notifyItemChanged) {
            val position = fastAdapter.getPosition(item.identifier)
            fastAdapter.notifyItemChanged(position)
        }
    }

    /**
     * Opens the expandable item at the given position with parents who contains this item
     *
     * @param position          the global position
     * @param notifyItemChanged true if we need to call notifyItemChanged. DEFAULT: false
     */
    @JvmOverloads
    fun expandIncludeParents(position: Int, notifyItemChanged: Boolean = false) {
        val parents = getExpandableParents(position)

        parents.forEach { expand(fastAdapter.getPosition(it.identifier)) }

        //we need to notify to get the correct drawable if there is one showing the current state
        if (notifyItemChanged) {
            fastAdapter.notifyItemChanged(position)
        }
    }

    /**
     * Calculates the count of expanded items before a given position
     *
     * @param from     the global start position you should pass here the count of items of the previous adapters (or 0 if you want to start from the beginning)
     * @param position the global position
     * @return the count of expandable items before a given position
     */
    fun getExpandedItemsCount(from: Int, position: Int): Int {
        return (from until position)
                .asSequence()
                .mapNotNull { fastAdapter.getItem(it) as? IExpandable<*> }
                .filter { it.isExpanded }
                .map { it.subItems.size }
                .sum()
    }

    /** Walks through the parents tree while parents are non-null and parents are IExpandable */
    private fun getExpandableParents(position: Int): List<IExpandable<*>> {
        val expandable = fastAdapter.getItem(position) as? IExpandable<*> ?: return emptyList()

        return getExpandableParents(expandable)
    }

    /** Walks through the parents tree while parents are non-null and parents are IExpandable */
    private fun getExpandableParents(expandable: IExpandable<*>): List<IExpandable<*>> {

        // walk through the parents tree
        val parents = mutableListOf<IExpandable<*>>()

        var element: IExpandable<*>? = expandable
        while (element != null) {
            parents.add(element)
            element = element.parent as? IExpandable<*>
        }

        // we need to reverse parents for going from root to provided item
        return parents.reversed()
    }

    companion object {
        private const val BUNDLE_EXPANDED = "bundle_expanded"

        init {
            ExtensionsFactories.register(ExpandableExtensionFactory())
        }
    }
}
