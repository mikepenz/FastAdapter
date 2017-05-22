package com.mikepenz.fastadapter_extensions.drag;

public interface ItemTouchCallback {

    /**
     * Called when an item has been dragged
     * This event is called on every item in a dragging chain
     *
     * @param oldPosition start position
     * @param newPosition end position
     * @return true if moved otherwise false
     */
    boolean itemTouchOnMove(int oldPosition, int newPosition);

    /**
     * Called when an item has been dropped
     * This event is only called once when the user stopped dragging the item
     *
     * @param oldPosition start position
     * @param newPosition end position
     */
    void itemTouchDropped(int oldPosition, int newPosition);
}