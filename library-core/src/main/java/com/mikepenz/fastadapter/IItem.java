package com.mikepenz.fastadapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by mikepenz on 03.02.15.
 */
public interface IItem<T, VH extends RecyclerView.ViewHolder> extends IIdentifyable<T> {

    /**
     * return a Tag of the Item
     *
     * @return
     */
    Object getTag();

    /**
     * set the Tag of the Item
     *
     * @param tag
     * @return
     */
    T withTag(Object tag);

    /**
     * return if the item is enabled
     *
     * @return
     */
    boolean isEnabled();

    /**
     * set if the item is enabled
     *
     * @param enabled
     * @return
     */
    T withEnabled(boolean enabled);

    /**
     * return if the item is selected
     *
     * @return
     */
    boolean isSelected();

    /**
     * set if the item is selected
     *
     * @param selected
     * @return
     */
    T withSetSelected(boolean selected);

    /**
     * return if the item is selectable
     *
     * @return
     */
    boolean isSelectable();

    /**
     * set if the item is selectable
     *
     * @param selectable
     * @return
     */
    T withSelectable(boolean selectable);

    /**
     * returns the type of the Item. Can be a hardcoded INT, but preferred is a defined id
     *
     * @return
     */
    @IdRes
    int getType();

    /**
     * returns the layout for the given item
     *
     * @return
     */
    @LayoutRes
    int getLayoutRes();

    /**
     * generates a view by the defined LayoutRes
     *
     * @param ctx
     * @return
     */
    View generateView(Context ctx);

    /**
     * generates a view by the defined LayoutRes and pass the LayoutParams from the parent
     *
     * @param ctx
     * @param parent
     * @return
     */
    View generateView(Context ctx, ViewGroup parent);

    /**
     * Generates a ViewHolder from this Item with the given parent
     *
     * @param parent
     * @return
     */
    VH getViewHolder(ViewGroup parent);

    /**
     * Binds the data of this item to the given holder
     *
     * @param holder
     * @param payloads
     */
    void bindView(VH holder, List<Object> payloads);

    /**
     * View needs to release resources when its recycled
     *
     * @param holder
     */
    void unbindView(VH holder);

    /**
     * View got attached to the window
     *
     * @param holder
     */
    void attachToWindow(VH holder);

    /**
     * View got detached from the window
     *
     * @param holder
     */
    void detachFromWindow(VH holder);

    /**
     * View is in a transient state and could not be recycled
     *
     * @param holder
     * @return return true if you want to recycle anyways (after clearing animations or so)
     */
    boolean failedToRecycle(VH holder);

    /**
     * If this item equals to the given identifier
     *
     * @param id
     * @return
     */
    boolean equals(int id);

}
