package com.mikepenz.fastadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Kotlin type alias to simplify usage for an all accepting item
 */
typealias GenericItemVHFactory = IItemVHFactory<out RecyclerView.ViewHolder>

/**
 * Defines the factory which is capable of creating the ViewHolder for a given Item
 */
interface IItemVHFactory<VH : RecyclerView.ViewHolder> {
    /** Generates a ViewHolder from this Item with the given parent */
    fun getViewHolder(parent: ViewGroup): VH
}