package com.mikepenz.fastadapter.items;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.IClickable;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.Collections;
import java.util.List;

/**
 * Created by mikepenz on 14.07.15.
 * Implements the general methods of the IItem interface to speed up development.
 */
public abstract class AbstractItem<Item extends IItem & IClickable, VH extends RecyclerView.ViewHolder> implements IItem<Item, VH>, IClickable<Item> {

    // the identifier for this item
    protected long mIdentifier = -1;

    /**
     * set the identifier of this item
     *
     * @param identifier
     * @return
     */
    public Item withIdentifier(long identifier) {
        this.mIdentifier = identifier;
        return (Item) this;
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
    public Item withTag(Object object) {
        this.mTag = object;
        return (Item) this;
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
    public Item withEnabled(boolean enabled) {
        this.mEnabled = enabled;
        return (Item) this;
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
    public Item withSetSelected(boolean selected) {
        this.mSelected = selected;
        return (Item) this;
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
    public Item withSelectable(boolean selectable) {
        this.mSelectable = selectable;
        return (Item) this;
    }

    /**
     * @return if this item is selectable
     */
    @Override
    public boolean isSelectable() {
        return mSelectable;
    }

    //this listener is called before any processing is done within the fastAdapter (comes before the FastAdapter item pre click listener)
    protected OnClickListener<Item> mOnItemPreClickListener;

    /**
     * provide a listener which is called before any processing is done within the adapter
     * return true if you want to consume the event
     *
     * @param onItemPreClickListener the listener
     * @return this
     */
    @Override
    public Item withOnItemPreClickListener(OnClickListener<Item> onItemPreClickListener) {
        mOnItemPreClickListener = onItemPreClickListener;
        return (Item) this;
    }

    /**
     * @return the on PRE item click listener
     */
    public OnClickListener<Item> getOnPreItemClickListener() {
        return mOnItemPreClickListener;
    }

    //listener called after the operations were done on click (comes before the FastAdapter item click listener)
    protected OnClickListener<Item> mOnItemClickListener;

    /**
     * provide a listener which is called before the click listener is called within the adapter
     * return true if you want to consume the event
     *
     * @param onItemClickListener the listener
     * @return this
     */
    @Override
    public Item withOnItemClickListener(OnClickListener<Item> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return (Item) this;
    }

    /**
     * @return the OnItemClickListener
     */
    public OnClickListener<Item> getOnItemClickListener() {
        return mOnItemClickListener;
    }

    /**
     * Binds the data of this item to the given holder
     *
     * @param holder
     * @param payloads
     */
    @Override
    @CallSuper
    public void bindView(final VH holder, List<Object> payloads) {
        //set the selected state of this item. force this otherwise it may is missed when implementing an item
        holder.itemView.setSelected(isSelected());
    }

    /**
     * View needs to release resources when its recycled
     *
     * @param holder
     */
    @Override
    public void unbindView(VH holder) {

    }

    /**
     * View got attached to the window
     *
     * @param holder
     */
    @Override
    public void attachToWindow(VH holder) {

    }

    /**
     * View got detached from the window
     *
     * @param holder
     */
    @Override
    public void detachFromWindow(VH holder) {

    }

    /**
     * RecyclerView was not able to recycle that viewHolder because it's in a transient state
     * Implement this and clear any animations, to allow recycling. Return true in that case
     *
     * @param holder
     * @return true if you want it to get recycled
     */
    @Override
    public boolean failedToRecycle(VH holder) {
        return false;
    }

    /**
     * this method is called by generateView(Context ctx), generateView(Context ctx, ViewGroup parent) and getViewHolder(ViewGroup parent)
     * it will generate the View from the layout, overwrite this if you want to implement your view creation programatically
     *
     * @param ctx
     * @param parent
     * @return
     */
    public View createView(Context ctx, @Nullable ViewGroup parent) {
        return LayoutInflater.from(ctx).inflate(getLayoutRes(), parent, false);
    }

    /**
     * generates a view by the defined LayoutRes
     *
     * @param ctx
     * @return
     */
    @Override
    public View generateView(Context ctx) {
        VH viewHolder = getViewHolder(createView(ctx, null));

        //as we already know the type of our ViewHolder cast it to our type
        bindView(viewHolder, Collections.EMPTY_LIST);

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
        VH viewHolder = getViewHolder(createView(ctx, parent));

        //as we already know the type of our ViewHolder cast it to our type
        bindView(viewHolder, Collections.EMPTY_LIST);
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
    public VH getViewHolder(ViewGroup parent) {
        return getViewHolder(createView(parent.getContext(), parent));
    }


    /**
     * This method returns the ViewHolder for our item, using the provided View.
     *
     * @param v
     * @return the ViewHolder for this Item
     */
    @NonNull
    public abstract VH getViewHolder(View v);

    /**
     * If this item equals to the given identifier
     *
     * @param id identifier
     * @return true if identifier equals id, false otherwise
     */
    @Override
    public boolean equals(int id) {
        return id == getIdentifier();
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
        return getIdentifier() == that.getIdentifier();
    }

    /**
     * the hashCode implementation
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Long.valueOf(getIdentifier()).hashCode();
    }
}
