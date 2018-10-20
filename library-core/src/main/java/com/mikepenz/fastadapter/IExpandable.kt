package com.mikepenz.fastadapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by mikepenz on 30.12.15.
 */
interface IExpandable<VH : RecyclerView.ViewHolder> : IParentItem<VH>, ISubItem<VH> {
    /**
     * True if expanded (opened)
     */
    var isExpanded: Boolean

    /**
     * True if the item should auto expand on click, false if you want to disable this
     */
    val isAutoExpanding: Boolean
}
