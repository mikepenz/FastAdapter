package com.mikepenz.fastadapter

import android.support.v7.widget.RecyclerView

/**
 * Created by fabianterhorst on 24.08.17.
 */

interface ITypeInstanceCache<Item : IItem<out RecyclerView.ViewHolder>> {

    fun register(item: Item): Boolean

    operator fun get(type: Int): Item

    fun clear()
}
