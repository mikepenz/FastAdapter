package com.mikepenz.fastadapter_extensions.drag;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.mikepenz.fastadapter_extensions.drag.IDraggable;
import com.mikepenz.fastadapter.IItem;

/**
 * Created by mikepenz on 30.12.15.
 */
public interface IExtendedDraggable<T, VH extends RecyclerView.ViewHolder, Item extends IItem> extends IDraggable<T, Item>
{

    /**
     * use this method to set the ItemTouchHelper reference in the item
     * this is necessary, so that the item can manually start the dragging
     * i.e when a drag icon within the item is touched
     *
     * @param itemTouchHelper the ItemTouchHelper
     * @return this
     */
    T withTouchHelper(ItemTouchHelper itemTouchHelper);

    /**
     * this returns the ItemTouchHelper
     *
     * @return the ItemTouchHelper if item has one or null
     */
    ItemTouchHelper getTouchHelper();

    /**
     * this method returns the drag view inside the item
     * use this with (@withTouchHelper) to start dragging when this view is touched
     *
     * @param viewHolder the ViewHolder
     * @return the view that should start the dragging or null
     */
    View getDragView(VH viewHolder);
}
