package com.mikepenz.fastadapter.binding

import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.IModelItem

/**
 * Provides a convenient abstract class for having a split model and item (with binding)
 */
abstract class ModelAbstractBindingItem<Model, Binding : ViewBinding>(override var model: Model) :
        AbstractBindingItem<Binding>(), IModelItem<Model, BindingViewHolder<Binding>>
