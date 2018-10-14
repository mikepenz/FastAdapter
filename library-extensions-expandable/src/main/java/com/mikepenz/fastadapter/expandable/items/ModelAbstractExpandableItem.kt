package com.mikepenz.fastadapter.expandable.items

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.items.ModelAbstractItem

abstract class ModelAbstractExpandableItem<Model, SubItem, Parent, VH : RecyclerView.ViewHolder>(
    model: Model
) : ModelAbstractItem<Model, VH>(model),
    IExpandable<SubItem, Parent> where Parent : IItem<out RecyclerView.ViewHolder>, Parent : IExpandable<SubItem, Parent>, SubItem : IItem<out RecyclerView.ViewHolder>, SubItem : ISubItem<Parent> {
    override var isExpanded: Boolean = false
    var parent: Parent? = null
    private var _subItems: List<SubItem>? = null
    override var subItems: List<SubItem>?
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