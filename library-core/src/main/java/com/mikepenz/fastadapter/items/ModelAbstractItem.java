package com.mikepenz.fastadapter.items;

import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.IModelItem;

/**
 * Created by mikepenz on 14.07.15.
 * Implements the general methods of the IItem interface to speed up development.
 */
public abstract class ModelAbstractItem<Model, Item extends ModelAbstractItem<?, ?, ?>, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> implements IModelItem<Model, Item, VH> {
    private Model mModel;

    public ModelAbstractItem(Model model) {
        this.mModel = model;
    }

    public Model getModel() {
        return mModel;
    }

    public ModelAbstractItem<?, ?, ?> withModel(Model model) {
        this.mModel = model;
        return this;
    }
}
