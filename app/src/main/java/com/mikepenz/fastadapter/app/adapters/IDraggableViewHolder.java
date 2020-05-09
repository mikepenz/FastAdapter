package com.mikepenz.fastadapter.app.adapters;

/**
 * Helper interface to allow a viewHolder to react to drag events.
 */
public interface IDraggableViewHolder {
    /**
     * Called when an item enters drag state
     */
    void onDragged();

    /**
     * Called when an item has been dropped
     */
    void onDropped();
}
