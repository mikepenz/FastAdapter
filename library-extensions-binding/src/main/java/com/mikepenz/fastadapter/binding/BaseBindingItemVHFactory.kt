package com.mikepenz.fastadapter.binding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.IItemVHFactory

/**
 * Implements an abstract item simplifying usage with ViewBinding.
 */
abstract class BaseBindingItemVHFactory<VB : ViewBinding, VH : BindingViewHolder<VB>> : IItemVHFactory<VH> {
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