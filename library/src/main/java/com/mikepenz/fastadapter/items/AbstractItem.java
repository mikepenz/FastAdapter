package com.mikepenz.fastadapter.items;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.lang.reflect.ParameterizedType;

/**
 * Created by mikepenz on 14.07.15.
 * Implements the general methods of the IItem interface to speed up development.
 */
public abstract class AbstractItem<T, VH extends RecyclerView.ViewHolder> implements IItem<T, VH> {
    // the identifier for this item
    protected long mIdentifier = -1;

    /**
     * set the identifier of this item
     *
     * @param identifier
     * @return
     */
    public T withIdentifier(long identifier) {
        this.mIdentifier = identifier;
        return (T) this;
    }

    /**
     * @return the identifier of this item
     */
    @Override
    public long getIdentifier() {
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


    @Override
    public void bindView(VH holder) {
        //set the selected state of this item. force this otherwise it may is missed when implementing an item
        holder.itemView.setSelected(isSelected());
        //set the tag of this item to this object (can be used when retrieving the view)
        holder.itemView.setTag(this);
    }

    /**
     * generates a view by the defined LayoutRes
     *
     * @param ctx
     * @return
     */
    @Override
    public View generateView(Context ctx) {
        RecyclerView.ViewHolder viewHolder = getViewHolder(LayoutInflater.from(ctx).inflate(getLayoutRes(), null, false));

        //as we already know the type of our ViewHolder cast it to our type
        bindView((VH) viewHolder);

        //return the bound view
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
        RecyclerView.ViewHolder viewHolder = getViewHolder(LayoutInflater.from(ctx).inflate(getLayoutRes(), parent, false));

        //as we already know the type of our ViewHolder cast it to our type
        bindView((VH) viewHolder);
        //return the bound and generatedView
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
        return getViewHolder(LayoutInflater.from(parent.getContext()).inflate(getLayoutRes(), parent, false));
    }


    /**
     * @Override
     * public ViewHolderFactory getFactory() {
     *      return new ItemFactory();
     * }
     *
     * public static class ItemFactory implements ViewHolderFactory<ViewHolder> {
     *      public ViewHolder factory(View v) {
     *          return new ViewHolder(v);
     *      }
     * }
     */
    /**
     * the abstract method to retrieve the ViewHolder factory
     * The ViewHolder factory implementation should look like (see the commented code above)
     *
     * @return
     */
    public ViewHolderFactory getFactory() {
        return null;
    }

    /**
     * This method returns the ViewHolder for our item, using the provided View.
     * By default it will try to get the ViewHolder from the ViewHolderFactory. If this one is not implemented it will go over the generic way, wasting ~5ms
     *
     * @param v
     * @return the ViewHolder for this Item
     */
    public VH getViewHolder(View v) {
        ViewHolderFactory viewHolderFactory = getFactory();

        if (viewHolderFactory != null) {
            return (VH) viewHolderFactory.create(v);
        } else {
            try {
                return (VH) ((Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1]).getDeclaredConstructor(View.class).newInstance(v);
            } catch (Exception e) {
                throw new RuntimeException("something really bad happened. if this happens more often, head over to GitHub and read how to switch to the ViewHolderFactory");
            }
        }
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
        AbstractItem<?, ?> that = (AbstractItem<?, ?>) o;
        return mIdentifier == that.mIdentifier;
    }

    /**
     * the hashCode implementation
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Long.valueOf(mIdentifier).hashCode();
    }
}
