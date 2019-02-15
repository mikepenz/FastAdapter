package com.mikepenz.fastadapter

import android.support.v7.widget.RecyclerView

interface IParentItem<VH : RecyclerView.ViewHolder> : IItem<VH> {

    /**
     * The list of subItems
     */
    var subItems: MutableList<out ISubItem<*>>?
}