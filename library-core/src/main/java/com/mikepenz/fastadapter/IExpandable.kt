package com.mikepenz.fastadapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by mikepenz on 30.12.15.
 */
interface IExpandable<Parent, SubItem> where Parent : IItem<out RecyclerView.ViewHolder>, Parent : IExpandable<Parent, *>, SubItem : IItem<out RecyclerView.ViewHolder>, SubItem : ISubItem<Parent> {
    /**
     * True if expanded (opened)
     */
    var isExpanded: Boolean

    /**
     * The list of subItems
     */
    var subItems: List<SubItem>?

    /**
     * True if the item should auto expand on click, false if you want to disable this
     */
    val isAutoExpanding: Boolean
}
