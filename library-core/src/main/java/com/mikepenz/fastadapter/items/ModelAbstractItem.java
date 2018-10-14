package com.mikepenz.fastadapter.items;

import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.IModelItem;

/**
 * Created by mikepenz on 14.07.15.
 * Implements the general methods of the IItem interface to speed up development.
 */
public abstract class ModelAbstractItem<Model, VH extends RecyclerView.ViewHolder> extends AbstractItem<VH> implements IModelItem<Model, VH> {
    private Model mModel;

    public ModelAbstractItem(Model model) {
        this.mModel = model;
    }

    @Override
    public Model getModel() {
        return mModel;
    }

    @Override
    public void setModel(Model model) {
        this.mModel = model;
    }
}
