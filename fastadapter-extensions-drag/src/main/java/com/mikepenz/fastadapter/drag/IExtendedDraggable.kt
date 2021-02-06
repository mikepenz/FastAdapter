package com.mikepenz.fastadapter.drag

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by mikepenz on 30.12.15.
 */
interface IExtendedDraggable<VH : RecyclerView.ViewHolder> : IDraggable {

    /**
     * This returns the ItemTouchHelper
     *
     * @return the ItemTouchHelper if item has one or null
     */
    val touchHelper: ItemTouchHelper?

    /**
     * This method returns the drag view inside the item
     * use this with (@withTouchHelper) to start dragging when this view is touched
     *
     * @param viewHolder the ViewHolder
     * @return the view that should start the dragging or null
     */
    fun getDragView(viewHolder: VH): View?
}
