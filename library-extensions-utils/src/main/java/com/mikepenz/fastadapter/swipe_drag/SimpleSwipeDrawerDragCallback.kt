@file:Suppress("PackageName", "PackageNaming")

package com.mikepenz.fastadapter.swipe_drag

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.drag.SimpleDragCallback
import com.mikepenz.fastadapter.swipe.SimpleSwipeDrawerCallback

/**
 * Created by Mattias on 2016-02-13.
 */
class SimpleSwipeDrawerDragCallback @JvmOverloads constructor(
        itemTouchCallback: ItemTouchCallback,
        swipeDirs: Int = ItemTouchHelper.LEFT) : SimpleDragCallback(itemTouchCallback) {

    private val simpleSwipeCallback: SimpleSwipeDrawerCallback
    private var defaultSwipeDirs: Int = 0

    init {
        setDefaultSwipeDirs(swipeDirs)
        simpleSwipeCallback = SimpleSwipeDrawerCallback(swipeDirs)
    }

    override fun setDefaultSwipeDirs(defaultSwipeDirs: Int) {
        this.defaultSwipeDirs = defaultSwipeDirs
        super.setDefaultSwipeDirs(defaultSwipeDirs)
    }

    fun withNotifyAllDrops(notifyAllDrops: Boolean): SimpleSwipeDrawerDragCallback {
        this.notifyAllDrops = notifyAllDrops
        return this
    }

    fun withSwipeLeft(widthDp: Int): SimpleSwipeDrawerDragCallback {
        simpleSwipeCallback.withSwipeLeft(widthDp)
        return this
    }

    fun withSwipeRight(widthDp: Int): SimpleSwipeDrawerDragCallback {
        simpleSwipeCallback.withSwipeRight(widthDp)
        return this
    }

    fun withSensitivity(f: Float): SimpleSwipeDrawerDragCallback {
        simpleSwipeCallback.withSensitivity(f)
        return this
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        simpleSwipeCallback.onSwiped(viewHolder, direction)
    }

    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return simpleSwipeCallback.getSwipeDirs(recyclerView, viewHolder)
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return simpleSwipeCallback.getSwipeEscapeVelocity(defaultValue)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return simpleSwipeCallback.getSwipeThreshold(viewHolder)
    }

    override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
    ) {
        simpleSwipeCallback.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        //Happen to know that our direct parent class doesn't (currently) draw anything...
        //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
