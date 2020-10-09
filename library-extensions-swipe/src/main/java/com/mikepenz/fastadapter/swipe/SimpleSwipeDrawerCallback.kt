package com.mikepenz.fastadapter.swipe

import android.graphics.Canvas
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem


/**
 * Created by Robb on 2020-07-04.
 */
class SimpleSwipeDrawerCallback @JvmOverloads constructor(private val swipeDirs: Int = ItemTouchHelper.LEFT) : ItemTouchHelper.SimpleCallback(0, swipeDirs) {

    // Swipe movement control
    private var sensitivityFactor = 1f

    // "Drawer width" swipe gesture is allowed to reach before blocking
    private var swipeWidthLeftDp = 20
    private var swipeWidthRightDp = 20

    // Indicates whether the touchTransmitter has been set on the RecyclerView
    private var touchTransmitterSet = false


    /**
     * Enable swipe to the left until the given width has been reached
     */
    fun withSwipeLeft(widthDp: Int): SimpleSwipeDrawerCallback {
        swipeWidthLeftDp = widthDp
        setDefaultSwipeDirs(swipeDirs or ItemTouchHelper.LEFT)
        return this
    }

    /**
     * Enable swipe to the right until the given width has been reached
     */
    fun withSwipeRight(widthDp: Int): SimpleSwipeDrawerCallback {
        swipeWidthRightDp = widthDp
        setDefaultSwipeDirs(swipeDirs or ItemTouchHelper.RIGHT)
        return this
    }

    /**
     * Control the sensitivity of the swipe gesture
     * 0.5 : very sensitive
     * 1 : Android default
     * 10 : almost insensitive
     */
    fun withSensitivity(f: Float): SimpleSwipeDrawerCallback {
        sensitivityFactor = f
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
        // Not used
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        // Not enabled
        return false
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * sensitivityFactor
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val itemView = viewHolder.itemView

        if (!touchTransmitterSet) {
            recyclerView.setOnTouchListener(RecyclerTouchTransmitter())
            touchTransmitterSet = true
        }

        if (viewHolder.adapterPosition == RecyclerView.NO_POSITION) {
            return
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val isLeft = dX < 0
            var swipeWidthPc = recyclerView.context.resources.displayMetrics.density / itemView.width
            swipeWidthPc *= if (isLeft) swipeWidthLeftDp else swipeWidthRightDp

            var swipeableView = itemView
            if (viewHolder is IDrawerSwipeableViewHolder) swipeableView = viewHolder.swipeableView

            swipeableView.translationX = dX * swipeWidthPc
        } else super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    /**
     * Hack to force-transmit click events to the first visible View at the clicked coordinates
     * [< swiped area ] exposed sublayer ]
     * Android default touch event mechanisms don't transmit these events to the sublayer :
     * any click on the exposed surface just swipe the item back to where it came
     */
    class RecyclerTouchTransmitter : View.OnTouchListener {

        override fun onTouch(v: View?, event: MotionEvent): Boolean {
            if (null == v || v !is ViewGroup) return false

            // Get the first visible View under the clicked coordinates
            val childView = v.getFirstVisibleViewByCoordinates(event.x, event.y)
            // Transmit the ACTION_DOWN and ACTION_UP events to this View
            if (childView != null)
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        childView.onTouchEvent(event)
                    }
                    MotionEvent.ACTION_UP -> {
                        childView.onTouchEvent(event)
                    }
                }
            return false
        }

        /**
         * Return the first visible non-ViewGroup View within the given ViewGroup, at the given coordinates
         */
        private fun ViewGroup.getFirstVisibleViewByCoordinates(x: Float, y: Float): View? {
            (childCount - 1 downTo 0)
                    .map { this.getChildAt(it) }
                    .forEach {
                        val bounds = Rect()
                        it.getHitRect(bounds)
                        if (bounds.contains(x.toInt(), y.toInt()) && VISIBLE == it.visibility) {
                            return if (it is ViewGroup) it.getFirstVisibleViewByCoordinates(x - bounds.left, y - bounds.top)
                            else it
                        }
                    }
            return null
        }
    }
}
