package com.mikepenz.fastadapter_extensions.drag;

import com.mikepenz.fastadapter.IItem;

/**
 * Created by mikepenz on 30.12.15.
 */
public interface IDraggable<T, Item extends IItem> {
    /**
     * @return true if draggable
     */
    boolean isDraggable();

    /**
     * use this method to set if item is draggable
     *
     * @param draggable true if draggable
     * @return this
     */
    T withIsDraggable(boolean draggable);
}
