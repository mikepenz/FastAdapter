package com.mikepenz.fastadapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by mikepenz on 30.12.15.
 */
interface IExpandable<SubItem> where SubItem : IItem<out RecyclerView.ViewHolder>, SubItem : ISubItem<*> {
    /**
     * True if expanded (opened)
     */
    var isExpanded: Boolean

    /**
     * The list of subItems
     */
    var subItems: List<SubItem>

    /**
     * True if the item should auto expand on click, false if you want to disable this
     */
    val isAutoExpanding: Boolean
}
