package com.mikepenz.fastadapter.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.lang.reflect.ParameterizedType;

/**
 * Created by mikepenz on 14.07.15.
 * Implements the general methods of the IItem interface to speed up development.
 */
public abstract class TypedAbstractItem<Model, Item extends IItem, VH extends RecyclerView.ViewHolder> extends AbstractItem<Item, VH> {
    private Model mModel;

    public TypedAbstractItem(Model model) {
        this.mModel = model;
    }

    public Model getModel() {
        return mModel;
    }

    public void setModel(Model model) {
        this.mModel = mModel;
    }

    @Override
    public VH getViewHolder(View v) {
        ViewHolderFactory viewHolderFactory = getFactory();

        if (viewHolderFactory != null) {
            return (VH) viewHolderFactory.create(v);
        } else {
            try {
                return (VH) ((Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[2]).getDeclaredConstructor(View.class).newInstance(v);
            } catch (Exception e) {
                throw new RuntimeException("something really bad happened. if this happens more often, head over to GitHub and read how to switch to the ViewHolderFactory");
            }
        }
    }
}
