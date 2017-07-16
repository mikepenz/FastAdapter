package com.mikepenz.fastadapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by mikepenz on 03.02.15.
 */
public interface IModelItem<Model, Item extends IModelItem<?, ?, ?>, VH extends RecyclerView.ViewHolder> extends IItem<Item, VH> {

    /**
     * set's the model of the item
     *
     * @param model
     * @return
     */
    IModelItem<?, ?, ?> withModel(Model model);

    /**
     * get's the model of the item
     *
     * @return the model
     */
    Model getModel();
}
