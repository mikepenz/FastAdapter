package com.mikepenz.fastadapter.swipe

import androidx.recyclerview.widget.ItemTouchHelper

/**
 * Created by mikepenz on 30.12.15.
 */
interface ISwipeable {
    /** @return true if swipeable */
    val isSwipeable: Boolean

    /**
     * @return true if the provided direction is supported
     *
     * This function will only be called if [isSwipeable] returns true
     */
    fun isDirectionSupported(direction: Int) : Boolean = true
}
