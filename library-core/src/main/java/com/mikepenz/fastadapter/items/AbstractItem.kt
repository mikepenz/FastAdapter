package com.mikepenz.fastadapter.items

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem
import java.util.*

/**
 * Created by mikepenz on 14.07.15.
 * Implements the general methods of the IItem interface to speed up development.
 */
abstract class AbstractItem<VH : RecyclerView.ViewHolder> : IItem<VH> {

    /**
     * The identifier of this item
     */
    override var identifier: Long = -1

    /**
     * The tag of this item
     */
    override var tag: Any? = null

    /**
     * If this item is enabled
     */
    override var isEnabled = true

    /**
     * If this item is selected
     */
    override var isSelected = false

    /**
     * If this item is selectable
     */
    override var isSelectable = true

    /**
     * Binds the data of this item to the given holder
     *
     * @param holder
     * @param payloads
     */
    @CallSuper
    override fun bindView(holder: VH, payloads: MutableList<Any>) {
        //set the selected state of this item. force this otherwise it may is missed when implementing an item
        holder.itemView.isSelected = isSelected
    }

    /**
     * View needs to release resources when its recycled
     *
     * @param holder
     */
    override fun unbindView(holder: VH) {
    }

    /**
     * View got attached to the window
     *
     * @param holder
     */
    override fun attachToWindow(holder: VH) {
    }

    /**
     * View got detached from the window
     *
     * @param holder
     */
    override fun detachFromWindow(holder: VH) {
    }

    /**
     * RecyclerView was not able to recycle that viewHolder because it's in a transient state
     * Implement this and clear any animations, to allow recycling. Return true in that case
     *
     * @param holder
     * @return true if you want it to get recycled
     */
    override fun failedToRecycle(holder: VH): Boolean {
        return false
    }

    /**
     * this method is called by generateView(Context ctx), generateView(Context ctx, ViewGroup parent) and getViewHolder(ViewGroup parent)
     * it will generate the View from the layout, overwrite this if you want to implement your view creation programatically
     *
     * @param ctx
     * @param parent
     * @return
     */
    open fun createView(ctx: Context, parent: ViewGroup?): View {
        return LayoutInflater.from(ctx).inflate(layoutRes, parent, false)
    }

    /**
     * generates a view by the defined LayoutRes
     *
     * @param ctx
     * @return
     */
    override fun generateView(ctx: Context): View {
        val viewHolder = getViewHolder(createView(ctx, null))

        //as we already know the type of our ViewHolder cast it to our type
        bindView(viewHolder, Collections.emptyList())

        //return the bound view
        return viewHolder.itemView
    }

    /**
     * generates a view by the defined LayoutRes and pass the LayoutParams from the parent
     *
     * @param ctx
     * @param parent
     * @return
     */
    override fun generateView(ctx: Context, parent: ViewGroup): View {
        val viewHolder = getViewHolder(createView(ctx, parent))

        //as we already know the type of our ViewHolder cast it to our type
        bindView(viewHolder, Collections.emptyList())
        //return the bound and generatedView
        return viewHolder.itemView
    }

    /**
     * Generates a ViewHolder from this Item with the given parent
     *
     * @param parent
     * @return
     */
    override fun getViewHolder(parent: ViewGroup): VH {
        return getViewHolder(createView(parent.context, parent))
    }


    /**
     * This method returns the ViewHolder for our item, using the provided View.
     *
     * @param v
     * @return the ViewHolder for this Item
     */
    abstract fun getViewHolder(v: View): VH

    /**
     * If this item equals to the given identifier
     *
     * @param id identifier
     * @return true if identifier equals id, false otherwise
     */
    override fun equals(id: Int): Boolean {
        return id.toLong() == identifier
    }

    /**
     * If this item equals to the given object
     *
     * @param o
     * @return
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as? AbstractItem<*>?
        return identifier == that?.identifier
    }

    /**
     * the hashCode implementation
     *
     * @return
     */
    override fun hashCode(): Int {
        return java.lang.Long.valueOf(identifier).hashCode()
    }
}
