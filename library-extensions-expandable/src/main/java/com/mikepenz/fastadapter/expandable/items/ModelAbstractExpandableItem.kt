package com.mikepenz.fastadapter.expandable.items

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.items.ModelAbstractItem

abstract class ModelAbstractExpandableItem<Model, Parent, SubItem, VH : RecyclerView.ViewHolder>(
    model: Model
) : ModelAbstractItem<Model, VH>(model),
    IExpandable<Parent, SubItem, VH> where Parent : IExpandable<Parent, SubItem, VH>, SubItem : ISubItem<Parent, VH> {
    override var isExpanded: Boolean = false
    var parent: Parent? = null
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
