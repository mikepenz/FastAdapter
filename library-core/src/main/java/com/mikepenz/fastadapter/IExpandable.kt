package com.mikepenz.fastadapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by mikepenz on 30.12.15.
 */
interface IExpandable<Parent, SubItem, VH : RecyclerView.ViewHolder> :
    IItem<VH> where Parent : IExpandable<Parent, *, VH>, SubItem : ISubItem<Parent, out RecyclerView.ViewHolder> {
    /**
     * True if expanded (opened)
     */
    var isExpanded: Boolean

    /**
     * The list of subItems
     */
    var subItems: MutableList<SubItem>?

    /**
     * True if the item should auto expand on click, false if you want to disable this
     */
    val isAutoExpanding: Boolean
}
