package com.mikepenz.fastadapter.binding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.IItemVHFactory

/**
 * Implements an abstract item simplifying usage with ViewBinding.
 */
abstract class AbstractBindingItem<VB : ViewBinding, VH : BindingViewHolder<VB>> : IItem<VH> {

    /** The identifier of this item */
    override var identifier: Long = -1L

    /** The tag of this item */
    override var tag: Any? = null

    /** The factory to use for creating this item, this does not have to be provided if the IItemFactory is implemented by this item too */
    override val factory: IItemVHFactory<VH>? = null

    /** If this item is enabled */
    override var isEnabled: Boolean = true

    /** If this item is selected */
    override var isSelected: Boolean = false

    /** If this item is selectable */
    override var isSelectable: Boolean = true

    /** Binds the data of this item to the given holder */
    @CallSuper
    override fun bindView(holder: VH, payloads: MutableList<Any>) {
        //set the selected state of this item. force this otherwise it may is missed when implementing an item
        holder.itemView.isSelected = isSelected
    }

    /** View needs to release resources when its recycled */
    override fun unbindView(holder: VH) {
    }

    /** View got attached to the window */
    override fun attachToWindow(holder: VH) {
    }

    /** View got detached from the window */
    override fun detachFromWindow(holder: VH) {
    }

    /**
     * RecyclerView was not able to recycle that viewHolder because it's in a transient state
     * Implement this and clear any animations, to allow recycling. Return true in that case
     *
     * @return true if you want it to get recycled
     */
    override fun failedToRecycle(holder: VH): Boolean {
        return false
    }

    /**
     * If this item equals to the given identifier
     *
     * @param id identifier
     * @return true if identifier equals id, false otherwise
     */
    override fun equals(id: Int): Boolean = id.toLong() == identifier

    /** If this item equals to the given object */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as? AbstractBindingItem<*, *>?
        return identifier == that?.identifier
    }

    /** The hashCode implementation */
    override fun hashCode(): Int = identifier.hashCode()
}

/**
 * Implements an abstract item simplifying usage with ViewBinding.
 */
abstract class AbstractBindingItemVHFactory<VB : ViewBinding, VH : BindingViewHolder<VB>> : IItemVHFactory<VH> {
    /** The layout res of this item, not needed */
    override val layoutRes: Int = -1

    /**
     * This method is called by generateView(Context ctx), generateView(Context ctx, ViewGroup parent) and getViewHolder(ViewGroup parent)
     * it will generate the ViewBinding. You have to provide the correct binding class.
     */
    abstract fun createBinding(inflater: LayoutInflater, parent: ViewGroup? = null): VB

    /** Generates a ViewHolder from this Item with the given parent */
    override fun getViewHolder(parent: ViewGroup): VH {
        return getViewHolder(createBinding(LayoutInflater.from(parent.context), parent))
    }

    /** Generates a ViewHolder from this Item with the given ViewBinding */
    abstract fun getViewHolder(viewBinding: VB): VH
}