package com.mikepenz.fastadapter

import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Kotlin type alias to simplify usage for an all accepting item
 */
typealias GenericItem = IItem<out RecyclerView.ViewHolder>

/**
 * Created by mikepenz on 03.02.15.
 */
interface IItem<VH : RecyclerView.ViewHolder> : IIdentifyable {

    /** A Tag of the Item */
    var tag: Any?

    /** If the item is enabled */
    var isEnabled: Boolean

    /** If the item is selected */
    var isSelected: Boolean

    /** If the item is selectable */
    var isSelectable: Boolean

    /** The type of the Item. Can be a hardcoded INT, but preferred is a defined id */
    @get:IdRes
    val type: Int

    /** The factory to use for creating this item, this does not have to be provided if the IItemFactory is implemented by this item too */
    val factory: IItemVHFactory<VH>?

    /** Binds the data of this item to the given holder */
    fun bindView(holder: VH, payloads: List<Any>)

    /** View needs to release resources when its recycled */
    fun unbindView(holder: VH)

    /** View got attached to the window */
    fun attachToWindow(holder: VH)

    /** View got detached from the window */
    fun detachFromWindow(holder: VH)

    /**
     * View is in a transient state and could not be recycled
     *
     * @param holder
     * @return return true if you want to recycle anyways (after clearing animations or so)
     */
    fun failedToRecycle(holder: VH): Boolean

    /** If this item equals to the given identifier */
    fun equals(id: Int): Boolean
}
