package com.mikepenz.fastadapter.swipe

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import kotlin.math.abs


/**
 * Created by Mattias on 2016-02-13.
 */
class SimpleSwipeCallback @JvmOverloads constructor(private val itemSwipeCallback: ItemSwipeCallback, private var leaveBehindDrawableLeft: Drawable?, private val swipeDirs: Int = ItemTouchHelper.LEFT, @param:ColorInt private var bgColorLeft: Int = Color.RED) : ItemTouchHelper.SimpleCallback(0, swipeDirs) {
    private var bgColorRight: Int = 0
    private var leaveBehindDrawableRight: Drawable? = null

    private var bgPaint: Paint? = null
    private var horizontalMargin = Integer.MAX_VALUE

    // Swipe movement control
    private var sensitivityFactor = 1f
    private var surfaceThreshold = 0.5f

    interface ItemSwipeCallback {

        /**
         * Called when an item has been swiped
         *
         * @param position  position of item in the adapter
         * @param direction direction the item was swiped
         */
        fun itemSwiped(position: Int, direction: Int)
    }

    fun withLeaveBehindSwipeLeft(d: Drawable): SimpleSwipeCallback {
        this.leaveBehindDrawableLeft = d
        setDefaultSwipeDirs(swipeDirs or ItemTouchHelper.LEFT)
        return this
    }

    fun withLeaveBehindSwipeRight(d: Drawable): SimpleSwipeCallback {
        this.leaveBehindDrawableRight = d
        setDefaultSwipeDirs(swipeDirs or ItemTouchHelper.RIGHT)
        return this
    }

    fun withHorizontalMarginDp(ctx: Context, dp: Int): SimpleSwipeCallback {
        return withHorizontalMarginPx((ctx.resources.displayMetrics.density * dp).toInt())
    }

    fun withHorizontalMarginPx(px: Int): SimpleSwipeCallback {
        horizontalMargin = px
        return this
    }

    fun withBackgroundSwipeLeft(@ColorInt bgColor: Int): SimpleSwipeCallback {
        bgColorLeft = bgColor
        setDefaultSwipeDirs(swipeDirs or ItemTouchHelper.LEFT)
        return this
    }

    fun withBackgroundSwipeRight(@ColorInt bgColor: Int): SimpleSwipeCallback {
        bgColorRight = bgColor
        setDefaultSwipeDirs(swipeDirs or ItemTouchHelper.RIGHT)
        return this
    }

    /**
     * Control the sensitivity of the swipe gesture
     * 0.5 : very sensitive
     * 1 : Android default
     * 10 : almost insensitive
     */
    fun withSensitivity(f: Float): SimpleSwipeCallback {
        sensitivityFactor = f
        return this
    }

    /**
     * % of the item's width or height needed to confirm the swipe action
     * Android default : 0.5
     */
    fun withSurfaceThreshold(f: Float): SimpleSwipeCallback {
        surfaceThreshold = f
        return this
    }

    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val item = FastAdapter.getHolderAdapterItem<IItem<*>>(viewHolder)
        return if (item is ISwipeable) {
            if ((item as ISwipeable).isSwipeable) {
                super.getSwipeDirs(recyclerView, viewHolder)
            } else {
                0
            }
        } else {
            super.getSwipeDirs(recyclerView, viewHolder)
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        viewHolder.itemView.translationX = 0f
        viewHolder.itemView.translationY = 0f
        val position = viewHolder.adapterPosition
        if (position != RecyclerView.NO_POSITION) {
            itemSwipeCallback.itemSwiped(position, direction)
        }
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        // not enabled
        return false
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * sensitivityFactor
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return surfaceThreshold
    }

    //Inspired/modified from: https://github.com/nemanja-kovacevic/recycler-view-swipe-to-delete/blob/master/app/src/main/java/net/nemanjakovacevic/recyclerviewswipetodelete/MainActivity.java
    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val itemView = viewHolder.itemView
        if (viewHolder.adapterPosition == RecyclerView.NO_POSITION) {
            return
        }
        if (abs(dX) > abs(dY)) {
            val isLeft = dX < 0
            if (bgPaint == null) {
                bgPaint = Paint()
                if (horizontalMargin == Integer.MAX_VALUE) {
                    withHorizontalMarginDp(recyclerView.context, 16)
                }
            }

            val bgPaint = this.bgPaint ?: return
            bgPaint.color = if (isLeft) bgColorLeft else bgColorRight

            if (bgPaint.color != Color.TRANSPARENT) {
                val left = if (isLeft) itemView.right + dX.toInt() else itemView.left
                val right = if (isLeft) itemView.right else itemView.left + dX.toInt()
                c.drawRect(left.toFloat(), itemView.top.toFloat(), right.toFloat(), itemView.bottom.toFloat(), bgPaint)
            }

            val drawable = if (isLeft) leaveBehindDrawableLeft else leaveBehindDrawableRight
            if (drawable != null) {
                val itemHeight = itemView.bottom - itemView.top
                val intrinsicWidth = drawable.intrinsicWidth
                val intrinsicHeight = drawable.intrinsicHeight

                val left: Int
                val right: Int
                if (isLeft) {
                    left = itemView.right - horizontalMargin - intrinsicWidth
                    right = itemView.right - horizontalMargin
                } else {
                    left = itemView.left + horizontalMargin
                    right = itemView.left + horizontalMargin + intrinsicWidth
                }
                val top = itemView.top + (itemHeight - intrinsicHeight) / 2
                val bottom = top + intrinsicHeight
                drawable.setBounds(left, top, right, bottom)

                drawable.draw(c)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
