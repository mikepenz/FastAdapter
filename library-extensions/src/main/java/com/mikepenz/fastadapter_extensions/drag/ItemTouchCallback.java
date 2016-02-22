package com.mikepenz.fastadapter_extensions.drag;

public interface ItemTouchCallback {

    /**
     * Called when an item has been dragged
     *
     * @param oldPosition start position
     * @param newPosition end position
     * @return true if moved otherwise false
     */
    boolean itemTouchOnMove(int oldPosition, int newPosition);
}