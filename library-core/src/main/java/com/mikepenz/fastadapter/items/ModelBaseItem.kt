package com.mikepenz.fastadapter.items

import androidx.recyclerview.widget.RecyclerView

import com.mikepenz.fastadapter.IModelItem

/**
 * Implements the general methods of the IItem interface to speed up development.
 */
abstract class ModelBaseItem<Model, VH : RecyclerView.ViewHolder>(override var model: Model) :
        BaseItem<VH>(), IModelItem<Model, VH>
