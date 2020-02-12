package com.mikepenz.fastadapter.binding

import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.IModelItem

/**
 * Implements an abstract model item simplifying usage with ViewBinding.
 */
abstract class ModelAbstractBindingItem<VB : ViewBinding, Model, VH : BindingViewHolder<VB>> :
        AbstractBindingItem<VB, VH>(), IModelItem<Model, VH>

