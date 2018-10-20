package com.mikepenz.fastadapter.expandable.items

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.items.AbstractItem

abstract class AbstractExpandableItem<Parent, SubItem, VH : RecyclerView.ViewHolder> :
    AbstractItem<VH>(),
    IExpandable<Parent, SubItem, VH> where Parent : IExpandable<Parent, SubItem, *>, SubItem : IExpandable<Parent, SubItem, *> {
    override var isExpanded: Boolean = false
    override var parent: Parent? = null
    private var _subItems: MutableList<SubItem>? = null
    override var subItems: MutableList<SubItem>?
        set(value) {
            _subItems = value
        }
        get() {
            _subItems?.let { subItems ->
                for (item in subItems) {
                    item.parent = parent
                }
            }
            return _subItems
        }
    override val isAutoExpanding: Boolean = true
    override var isSelectable: Boolean
        get() = _subItems?.isNotEmpty() == true
        set(value) {}
}
