package com.mikepenz.fastadapter.items

import androidx.recyclerview.widget.RecyclerView

import com.mikepenz.fastadapter.IModelItem

/**
 * Created by mikepenz on 14.07.15.
 * Implements the general methods of the IItem interface to speed up development.
 */
abstract class ModelAbstractItem<Model, VH : RecyclerView.ViewHolder>(override var model: Model) :
    AbstractItem<VH>(), IModelItem<Model, VH>
