package com.mikepenz.fastadapter

import android.support.v7.widget.RecyclerView

interface ISubItem<VH : RecyclerView.ViewHolder> : IItem<VH> {

    /**
     * Use this to get the parent of this sub item
     * the parent should also contain this sub item in its sub items list
     */
    var parent: IParentItem<*>?
}
