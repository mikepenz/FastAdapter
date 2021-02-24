package com.mikepenz.fastadapter.binding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.IItemVHFactory
import com.mikepenz.fastadapter.items.BaseItem

/**
 * Implements an abstract item simplifying usage with ViewBinding as much as possible.
 */
abstract class AbstractBindingItem<Binding : ViewBinding> : BaseItem<BindingViewHolder<Binding>>(), IItemVHFactory<BindingViewHolder<Binding>> {

    @CallSuper
    override fun bindView(holder: BindingViewHolder<Binding>, payloads: List<Any>) {
        super.bindView(holder, payloads)
        bindView(holder.binding, payloads)
    }

    open fun bindView(binding: Binding, payloads: List<Any>) {}

    override fun unbindView(holder: BindingViewHolder<Binding>) {
        super.unbindView(holder)
        unbindView(holder.binding)
    }

    open fun unbindView(binding: Binding) {}

    override fun attachToWindow(holder: BindingViewHolder<Binding>) {
        super.attachToWindow(holder)
        attachToWindow(holder.binding)
    }

    open fun attachToWindow(binding: Binding) {}

    override fun detachFromWindow(holder: BindingViewHolder<Binding>) {
        super.detachFromWindow(holder)
        detachFromWindow(holder.binding)
    }

    open fun detachFromWindow(binding: Binding) {}

    /**
     * This method is called by generateView(Context ctx), generateView(Context ctx, ViewGroup parent) and getViewHolder(ViewGroup parent)
     * it will generate the ViewBinding. You have to provide the correct binding class.
     */
    abstract fun createBinding(inflater: LayoutInflater, parent: ViewGroup? = null): Binding

    /** Generates a ViewHolder from this Item with the given parent */
    override fun getViewHolder(parent: ViewGroup): BindingViewHolder<Binding> {
        return getViewHolder(createBinding(LayoutInflater.from(parent.context), parent))
    }

    /** Generates a ViewHolder from this Item with the given ViewBinding */
    open fun getViewHolder(viewBinding: Binding): BindingViewHolder<Binding> {
        return BindingViewHolder(viewBinding)
    }
}