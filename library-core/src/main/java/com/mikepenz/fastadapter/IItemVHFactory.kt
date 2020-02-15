package com.mikepenz.fastadapter

import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Kotlin type alias to simplify usage for an all accepting item
 */
typealias GenericItemVHFactory = IItemVHFactory<out RecyclerView.ViewHolder>

/**
 * Defines the factory which is capable of creating the ViewHolder for a given Item
 */
interface IItemVHFactory<VH : RecyclerView.ViewHolder> {

    /** The type of the Item. Can be a hardcoded INT, but preferred is a defined id */
    @get:IdRes
    val type: Int

    /** The layout for the given item */
    @get:LayoutRes
    val layoutRes: Int

    /** Generates a ViewHolder from this Item with the given parent */
    fun getViewHolder(parent: ViewGroup): VH
}