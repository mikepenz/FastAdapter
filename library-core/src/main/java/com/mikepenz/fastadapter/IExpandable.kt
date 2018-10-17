package com.mikepenz.fastadapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by mikepenz on 30.12.15.
 */
interface IExpandable<Parent, SubItem, VH : RecyclerView.ViewHolder> :
    IItem<VH> where Parent : IExpandable<Parent, SubItem, VH>, SubItem : IExpandable<Parent, SubItem, out RecyclerView.ViewHolder> {
    /**
     * True if expanded (opened)
     */
    var isExpanded: Boolean

    /**
     * Use this to get the parent of this sub item
     * the parent should also contain this sub item in its sub items list
     */
    var parent: Parent?

    /**
     * The list of subItems
     */
    var subItems: MutableList<SubItem>?

    /**
     * True if the item should auto expand on click, false if you want to disable this
     */
    val isAutoExpanding: Boolean
}
