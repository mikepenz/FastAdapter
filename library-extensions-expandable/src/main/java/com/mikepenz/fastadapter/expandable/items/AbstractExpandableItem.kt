package com.mikepenz.fastadapter.expandable.items

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.IParentItem
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.MutableSubItemList
import com.mikepenz.fastadapter.items.AbstractItem

abstract class AbstractExpandableItem<VH : RecyclerView.ViewHolder> :
        AbstractItem<VH>(),
        IItem<VH>,
        IExpandable<VH> {
    private val _subItems = MutableSubItemList<ISubItem<*>>(this)

    override var isExpanded: Boolean = false
    override var parent: IParentItem<*>? = null
    override var subItems: MutableList<ISubItem<*>>
        set(value) = _subItems.setNewList(value)
        get() = _subItems

    override val isAutoExpanding: Boolean = true
    override var isSelectable: Boolean
        get() = subItems.isEmpty()
        set(_) {}
}
