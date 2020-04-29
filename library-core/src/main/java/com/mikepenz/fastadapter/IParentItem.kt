package com.mikepenz.fastadapter

import androidx.recyclerview.widget.RecyclerView

interface IParentItem<VH : RecyclerView.ViewHolder> : IItem<VH> {

    /** The list of subItems */
    var subItems: MutableList<ISubItem<*>>
}