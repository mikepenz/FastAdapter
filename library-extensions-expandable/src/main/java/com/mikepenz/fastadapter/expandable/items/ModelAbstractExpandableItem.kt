package com.mikepenz.fastadapter.expandable.items

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IParentItem
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.items.ModelAbstractItem

abstract class ModelAbstractExpandableItem<Model, Parent, SubItem, VH : RecyclerView.ViewHolder>(model: Model) : ModelAbstractItem<Model, VH>(model),
        IExpandable<VH> where Parent : IExpandable<VH>, SubItem : IExpandable<VH> {
    override var isExpanded: Boolean = false
    override var parent: IParentItem<*>? = null
    private var _subItems: MutableList<ISubItem<*>> = mutableListOf()
    override var subItems: MutableList<ISubItem<*>>
        set(value) {
            _subItems = value
            _subItems.let { subItems ->
                for (item in subItems) {
                    item.parent = this
                }
            }
        }
        get() {
            return _subItems
        }
    override val isAutoExpanding: Boolean = true
    override var isSelectable: Boolean
        get() = _subItems.isNotEmpty()
        set(value) {}
}
