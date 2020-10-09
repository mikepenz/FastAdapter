@file:Suppress("PackageName", "PackageNaming")

package com.mikepenz.fastadapter.swipe_drag

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.drag.ItemTouchCallback
import com.mikepenz.fastadapter.drag.SimpleDragCallback
import com.mikepenz.fastadapter.swipe.SimpleSwipeCallback

/**
 * Created by Mattias on 2016-02-13.
 */
class SimpleSwipeDragCallback @JvmOverloads constructor(
        itemTouchCallback: ItemTouchCallback,
        itemSwipeCallback: SimpleSwipeCallback.ItemSwipeCallback,
        leaveBehindDrawable: Drawable?,
        swipeDirs: Int = ItemTouchHelper.LEFT,
        @ColorInt bgColor: Int = Color.RED
) : SimpleDragCallback(itemTouchCallback) {

    private val simpleSwipeCallback: SimpleSwipeCallback
    private var defaultSwipeDirs: Int = 0

    init {
        setDefaultSwipeDirs(swipeDirs)
        simpleSwipeCallback = SimpleSwipeCallback(itemSwipeCallback, leaveBehindDrawable, swipeDirs, bgColor)
    }

    override fun setDefaultSwipeDirs(defaultSwipeDirs: Int) {
        this.defaultSwipeDirs = defaultSwipeDirs
        super.setDefaultSwipeDirs(defaultSwipeDirs)
    }

    fun withLeaveBehindSwipeLeft(d: Drawable): SimpleSwipeDragCallback {
        setDefaultSwipeDirs(defaultSwipeDirs or ItemTouchHelper.LEFT)
        simpleSwipeCallback.withLeaveBehindSwipeLeft(d)
        return this
    }

    fun withLeaveBehindSwipeRight(d: Drawable): SimpleSwipeDragCallback {
        setDefaultSwipeDirs(defaultSwipeDirs or ItemTouchHelper.RIGHT)
        simpleSwipeCallback.withLeaveBehindSwipeRight(d)
        return this
    }

    fun withHorizontalMarginDp(ctx: Context, dp: Int): SimpleSwipeDragCallback {
        simpleSwipeCallback.withHorizontalMarginDp(ctx, dp)
        return this
    }

    fun withHorizontalMarginPx(px: Int): SimpleSwipeDragCallback {
        simpleSwipeCallback.withHorizontalMarginPx(px)
        return this
    }

    fun withBackgroundSwipeLeft(@ColorInt bgColor: Int): SimpleSwipeDragCallback {
        setDefaultSwipeDirs(defaultSwipeDirs or ItemTouchHelper.LEFT)
        simpleSwipeCallback.withBackgroundSwipeLeft(bgColor)
        return this
    }

    fun withBackgroundSwipeRight(@ColorInt bgColor: Int): SimpleSwipeDragCallback {
        setDefaultSwipeDirs(defaultSwipeDirs or ItemTouchHelper.RIGHT)
        simpleSwipeCallback.withBackgroundSwipeRight(bgColor)
        return this
    }

    fun withNotifyAllDrops(notifyAllDrops: Boolean): SimpleSwipeDragCallback {
        this.notifyAllDrops = notifyAllDrops
        return this
    }

    fun withSensitivity(f: Float): SimpleSwipeDragCallback {
        simpleSwipeCallback.withSensitivity(f)
        return this
    }

    fun withSurfaceThreshold(f: Float): SimpleSwipeDragCallback {
        simpleSwipeCallback.withSurfaceThreshold(f)
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
