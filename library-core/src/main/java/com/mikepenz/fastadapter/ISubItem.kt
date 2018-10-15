package com.mikepenz.fastadapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by mikepenz on 30.12.15.
 */
interface ISubItem<Parent, VH : RecyclerView.ViewHolder> :
    IItem<VH> where Parent : IExpandable<Parent, *, VH> {
    /**
     * Use this to get the parent of this sub item
     * the parent should also contain this sub item in its sub items list
     */
    var parent: Parent?
}
