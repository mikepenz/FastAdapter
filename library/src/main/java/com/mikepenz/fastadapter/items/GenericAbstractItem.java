package com.mikepenz.fastadapter.items;

import android.support.v7.widget.RecyclerView;

import java.lang.reflect.ParameterizedType;

/**
 * Created by mikepenz on 14.07.15.
 * Implements the general methods of the IItem interface to speed up development.
 */
public abstract class GenericAbstractItem<Model, Item extends GenericAbstractItem<?, ?, ?>, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> {
    private Model mModel;

    public GenericAbstractItem(Model model) {
        this.mModel = model;
    }

    public Model getModel() {
        return mModel;
    }

    public GenericAbstractItem<?, ?, ?> setModel(Model model) {
        this.mModel = model;
        return this;
    }

    @Override
    protected Class<? extends VH> viewHolderType() {
        return ((Class<? extends VH>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2]);
    }
}