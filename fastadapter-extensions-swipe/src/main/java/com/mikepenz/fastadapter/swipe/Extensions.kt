package com.mikepenz.fastadapter.swipe

import androidx.recyclerview.widget.ItemTouchHelper

/**
 * Retrieve the swipe directions allowed for this [ISwipeable] element.
 */
internal fun ISwipeable.getSwipeDirs(dirs: Int): Int {
    var directions = dirs
    if (!isDirectionSupported(ItemTouchHelper.LEFT)) {
        directions = dirs and ItemTouchHelper.LEFT.inv()
    }
    if (!isDirectionSupported(ItemTouchHelper.RIGHT)) {
        directions = dirs and ItemTouchHelper.RIGHT.inv()
    }
    return directions
}