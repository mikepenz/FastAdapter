package com.mikepenz.fastadapter.items;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IClickable;
import com.mikepenz.fastadapter.IItem;

import java.util.Collections;
import java.util.List;

/**
 * Created by mikepenz on 14.07.15.
 * Implements the general methods of the IItem interface to speed up development.
 */
public abstract class AbstractItem<Item extends IItem & IClickable, VH extends RecyclerView.ViewHolder> implements IItem<Item, VH>, IClickable<Item> {
    // the identifier for this item
    private long mIdentifier = -1;

    private static long currentIdentifier = 0;

    public AbstractItem() {
        this(--currentIdentifier);
    }

    public AbstractItem(long identifier) {
        this.mIdentifier = identifier;
    }

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
    protected FastAdapter.OnClickListener<Item> mOnItemPreClickListener;

    /**
     * provide a listener which is called before any processing is done within the adapter
     * return true if you want to consume the event
     *
     * @param onItemPreClickListener the listener
     * @return this
     */
    @Override
    public Item withOnItemPreClickListener(FastAdapter.OnClickListener<Item> onItemPreClickListener) {
        mOnItemPreClickListener = onItemPreClickListener;
        return (Item) this;
    }

    /**
     * @return the on PRE item click listener
     */
    public FastAdapter.OnClickListener<Item> getOnPreItemClickListener() {
        return mOnItemPreClickListener;
    }

    //listener called after the operations were done on click (comes before the FastAdapter item click listener)
    protected FastAdapter.OnClickListener<Item> mOnItemClickListener;

    /**
     * provide a listener which is called before the click listener is called within the adapter
     * return true if you want to consume the event
     *
     * @param onItemClickListener the listener
     * @return this
     */
    @Override
    public Item withOnItemClickListener(FastAdapter.OnClickListener<Item> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return (Item) this;
    }

    /**
     * @return the OnItemClickListener
     */
    public FastAdapter.OnClickListener<Item> getOnItemClickListener() {
        return mOnItemClickListener;
    }

    @Override
    @CallSuper
    public void bindView(final VH holder, List<Object> payloads) {
        //set the selected state of this item. force this otherwise it may is missed when implementing an item
        holder.itemView.setSelected(isSelected());
        //set the tag of this item to this object (can be used when retrieving the view)
        holder.itemView.setTag(this);
    }

    @Override
    public void unbindView(VH holder) {

    }

    /**
     * generates a view by the defined LayoutRes
     *
     * @param ctx
     * @return
     */
    @Override
    public View generateView(Context ctx) {
        VH viewHolder = getViewHolder(LayoutInflater.from(ctx).inflate(getLayoutRes(), null, false));

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
        VH viewHolder = getViewHolder(LayoutInflater.from(ctx).inflate(getLayoutRes(), parent, false));

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
        return getViewHolder(LayoutInflater.from(parent.getContext()).inflate(getLayoutRes(), parent, false));
    }


    /**
     * This method returns the ViewHolder for our item, using the provided View.
     *
     * @param v
     * @return the ViewHolder for this Item
     */
    public abstract VH getViewHolder(View v);

    /**
     * If this item equals to the given identifier
     *
     * @param id identifier
     * @return true if identifier equals id, false otherwise
     */
    @Override
    public boolean equals(int id) {
        return id == mIdentifier;
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
