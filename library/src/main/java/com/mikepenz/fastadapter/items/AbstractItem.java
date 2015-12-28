package com.mikepenz.fastadapter.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

/**
 * Created by mikepenz on 14.07.15.
 * Implements the general methods of the IItem interface to speed up development.
 */
public abstract class AbstractItem<T> implements IItem<T> {
    // the identifier for this item
    protected int mIdentifier = -1;

    /**
     * set the identifier of this item
     *
     * @param identifier
     * @return
     */
    public T withIdentifier(int identifier) {
        this.mIdentifier = identifier;
        return (T) this;
    }

    /**
     * @return the identifier of this item
     */
    @Override
    public int getIdentifier() {
        return mIdentifier;
    }

    // the tag for this item
    protected Object mTag;

    /**
     * set the tag of this item
     *
     * @param object
     * @return
     */
    public T withTag(Object object) {
        this.mTag = object;
        return (T) this;
    }

    /**
     * @return the tag of this item
     */
    @Override
    public Object getTag() {
        return mTag;
    }

    // defines if this item is enabled
    protected boolean mEnabled = true;

    /**
     * set if this item is enabled
     *
     * @param enabled true if this item is enabled
     * @return
     */
    public T withEnabled(boolean enabled) {
        this.mEnabled = enabled;
        return (T) this;
    }

    /**
     * @return if this item is enabled
     */
    @Override
    public boolean isEnabled() {
        return mEnabled;
    }

    // defines if the item is selected
    protected boolean mSelected = false;

    /**
     * set if this item is selected
     *
     * @param selected true if this item is selected
     * @return
     */
    @Override
    public T withSetSelected(boolean selected) {
        this.mSelected = selected;
        return (T) this;
    }

    /**
     * @return if this item is selected
     */
    @Override
    public boolean isSelected() {
        return mSelected;
    }

    // defines if this item is selectable
    protected boolean mSelectable = true;

    /**
     * set if this item is selectable
     *
     * @param selectable true if this item is selectable
     * @return
     */
    @Override
    public T withSelectable(boolean selectable) {
        this.mSelectable = selectable;
        return (T) this;
    }

    /**
     * @return if this item is selectable
     */
    @Override
    public boolean isSelectable() {
        return mSelectable;
    }

    /**
     * the abstract method to retrieve the ViewHolder factory
     * The ViewHolder factory implementation should look like
     * public ViewHolderFactory getFactory() {
     * return new ItemFactory();
     * }
     * public static class ItemFactory implements ViewHolderFactory<ViewHolder> {
     * public ViewHolder factory(View v) {
     * return new ViewHolder(v);
     * }
     * }
     *
     * @return
     */
    public abstract ViewHolderFactory getFactory();

    /**
     * generates a view by the defined LayoutRes
     *
     * @param ctx
     * @return
     */
    @Override
    public View generateView(Context ctx) {
        RecyclerView.ViewHolder viewHolder = getFactory().factory(LayoutInflater.from(ctx).inflate(getLayoutRes(), null, false));
        bindView(viewHolder);
        return viewHolder.itemView;
    }

    /**
     * generates a view by the defined LayoutRes and pass the LayoutParams from the parent
     *
     * @param ctx
     * @param parent
     * @return
     */
    @Override
    public View generateView(Context ctx, ViewGroup parent) {
        RecyclerView.ViewHolder viewHolder = getFactory().factory(LayoutInflater.from(ctx).inflate(getLayoutRes(), parent, false));
        bindView(viewHolder);
        return viewHolder.itemView;
    }

    /**
     * Generates a ViewHolder from this Item with the given parent
     *
     * @param parent
     * @return
     */
    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup parent) {
        return getFactory().factory(LayoutInflater.from(parent.getContext()).inflate(getLayoutRes(), parent, false));
    }

    /**
     * If this item equals to the given identifier
     *
     * @param id
     * @return
     */
    @Override
    public boolean equals(Integer id) {
        return id != null && id == mIdentifier;
    }

    /**
     * If this item equals to the given object
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractItem<?> that = (AbstractItem<?>) o;
        return mIdentifier == that.mIdentifier;
    }

    /**
     * the hashCode implementation
     *
     * @return
     */
    @Override
    public int hashCode() {
        return mIdentifier;
    }
}
