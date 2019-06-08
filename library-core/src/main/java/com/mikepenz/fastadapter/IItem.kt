package com.mikepenz.fastadapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by mikepenz on 03.02.15.
 */
interface IItem<VH : RecyclerView.ViewHolder> : IIdentifyable {

    /**
     * a Tag of the Item
     */
    var tag: Any?

    /**
     * If the item is enabled
     */
    var isEnabled: Boolean

    /**
     * If the item is selected
     */
    var isSelected: Boolean

    /**
     * If the item is selectable
     */
    var isSelectable: Boolean

    /**
     * The type of the Item. Can be a hardcoded INT, but preferred is a defined id
     */
    @get:IdRes
    val type: Int

    /**
     * The layout for the given item
     */
    @get:LayoutRes
    val layoutRes: Int

    /**
     * generates a view by the defined LayoutRes
     *
     * @param ctx
     * @return
     */
    fun generateView(ctx: Context): View

    /**
     * generates a view by the defined LayoutRes and pass the LayoutParams from the parent
     *
     * @param ctx
     * @param parent
     * @return
     */
    fun generateView(ctx: Context, parent: ViewGroup): View

    /**
     * Generates a ViewHolder from this Item with the given parent
     *
     * @param parent
     * @return
     */
    fun getViewHolder(parent: ViewGroup): VH

    /**
     * Binds the data of this item to the given holder
     *
     * @param holder
     * @param payloads
     */
    fun bindView(holder: VH, payloads: MutableList<Any>)

    /**
     * View needs to release resources when its recycled
     *
     * @param holder
     */
    fun unbindView(holder: VH)

    /**
     * View got attached to the window
     *
     * @param holder
     */
    fun attachToWindow(holder: VH)

    /**
     * View got detached from the window
     *
     * @param holder
     */
    fun detachFromWindow(holder: VH)

    /**
     * View is in a transient state and could not be recycled
     *
     * @param holder
     * @return return true if you want to recycle anyways (after clearing animations or so)
     */
    fun failedToRecycle(holder: VH): Boolean

    /**
     * If this item equals to the given identifier
     *
     * @param id
     * @return
     */
    fun equals(id: Int): Boolean
}
