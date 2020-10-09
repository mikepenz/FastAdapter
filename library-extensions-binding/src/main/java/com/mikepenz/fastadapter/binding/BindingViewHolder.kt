package com.mikepenz.fastadapter.binding

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * A Simple [ViewHolder] providing easier support for ViewBinding
 */
open class BindingViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)