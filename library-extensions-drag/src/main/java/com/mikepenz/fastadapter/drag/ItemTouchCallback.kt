package com.mikepenz.fastadapter.drag

import androidx.recyclerview.widget.RecyclerView

interface ItemTouchCallback {

    /**
     * Called when an item enters drag state
     *
     * @param viewHolder dragged ViewHolder
     */
    fun itemTouchStartDrag(viewHolder: RecyclerView.ViewHolder) {}

    /**
     * Called when an item has been dragged
     * This event is called on every item in a dragging chain
     *
     * @param oldPosition start position
     * @param newPosition end position
     * @return true if moved otherwise false
     */
    fun itemTouchOnMove(oldPosition: Int, newPosition: Int): Boolean

    /**
     * Called when an item has been dropped
     * This event is only called :
     *   - Once when the user stopped dragging the item
     *   - If the corresponding AbstractItem implements {@link IDraggable}
     *
     * @param oldPosition start position
     * @param newPosition end position
     */
    fun itemTouchDropped(oldPosition: Int, newPosition: Int) {}
}