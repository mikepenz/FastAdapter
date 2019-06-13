package com.mikepenz.fastadapter.expandable.items

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.IParentItem
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.items.AbstractItem

abstract class AbstractExpandableItem<VH : RecyclerView.ViewHolder> :
        AbstractItem<VH>(),
        IItem<VH>,
        IExpandable<VH> {
    override var isExpanded: Boolean = false
    override var parent: IParentItem<*>? = null

    override var subItems: MutableList<ISubItem<*>> = mutableListOf()
        set(value) {
            field = value
            for (item in value) {
                item.parent = this
            }
        }

    override val isAutoExpanding: Boolean = true
    override var isSelectable: Boolean
        get() = subItems.isNullOrEmpty()
        set(value) {}
}
