package com.mikepenz.fastadapter

import android.support.v7.widget.RecyclerView

/**
 * Created by mikepenz on 03.02.15.
 */
interface IModelItem<Model, VH : RecyclerView.ViewHolder> : IItem<VH> {

    /**
     * The model of the item
     */
    var model: Model
}
