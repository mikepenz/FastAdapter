package com.mikepenz.fastadapter.swipe

import androidx.recyclerview.widget.ItemTouchHelper

internal fun ISwipeable.getSwipeDirs(dirs: Int): Int {
    var dirs = dirs
    if (!isDirectionSupported(ItemTouchHelper.LEFT)) {
        dirs = dirs and ItemTouchHelper.LEFT.inv()
    }
    if (!isDirectionSupported(ItemTouchHelper.RIGHT)) {
        dirs = dirs and ItemTouchHelper.RIGHT.inv()
    }
    return dirs
}