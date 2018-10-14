package com.mikepenz.fastadapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by mikepenz on 30.12.15.
 */
interface ISubItem<Parent> where Parent: IItem<out RecyclerView.ViewHolder>, Parent : IExpandable<*, Parent> {
    /**
     * Use this to get the parent of this sub item
     * the parent should also contain this sub item in its sub items list
     */
    var parent: Parent
}
