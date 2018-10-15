package com.mikepenz.fastadapter.items

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.ISubItem

interface SubItem<Parent, VH> : ISubItem<Parent>,
    IItem<VH> where Parent : IItem<VH>, Parent : IExpandable<Parent, *>, VH : RecyclerView.ViewHolder