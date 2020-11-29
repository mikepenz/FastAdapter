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
import kotlin.math.abs


/**
 * Created by Robb on 2020-07-04.
 */
class SimpleSwipeDrawerCallback @JvmOverloads constructor(private val swipeDirs: Int = ItemTouchHelper.LEFT, private val itemSwipeCallback: ItemSwipeCallback? = null) : ItemTouchHelper.SimpleCallback(0, swipeDirs) {

    // Swipe movement control
    private var sensitivityFactor = 1f
    private var surfaceThreshold = 0.5f

    // "Drawer width" swipe gesture is allowed to reach before blocking
    private var swipeWidthLeftDp = 20
    private var swipeWidthRightDp = 20

    // Indicates whether the touchTransmitter has been set on the RecyclerView
    private var touchTransmitterSet = false

    // States of swiped items
    //  Key = item position
    //  Value = swiped direction (see {@link ItemTouchHelper})
    private val swipedStates = HashMap<Int, Int>()

    // True if a swiping gesture is currently being done
    var isSwiping = false

    interface ItemSwipeCallback {

        /**
         * Called when a drawer has been swiped
         *
         * @param position  position of item in the adapter
         * @param direction direction the item where the drawer was swiped (see {@link ItemTouchHelper})
         */
        fun itemSwiped(position: Int, direction: Int)

        /**
         * Called when a drawer has been un-swiped (= returns to its default position)
         *
         * @param position  position of item in the adapter
         */
        fun itemUnswiped(position: Int)
    }

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

    /**
     * % of the item's width or height needed to confirm the swipe action
     * Android default : 0.5
     */
    fun withSurfaceThreshold(f: Float): SimpleSwipeDrawerCallback {
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
        val position = viewHolder.adapterPosition
        if (position != RecyclerView.NO_POSITION && (!swipedStates.containsKey(position) || swipedStates[position] != direction)) {
            itemSwipeCallback?.itemSwiped(position, direction)
            swipedStates[position] = direction
            isSwiping = false
        }
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        // Not enabled
        return false
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * sensitivityFactor
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        // During the "unswipe" gesture, Android doesn't use the threshold value properly
        // => Need to communicate an inverted value for swiped items
        return if (swipedStates.containsKey(viewHolder.adapterPosition)) 1f - surfaceThreshold
        else surfaceThreshold
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val itemView = viewHolder.itemView

        if (!touchTransmitterSet) {
            recyclerView.setOnTouchListener(RecyclerTouchTransmitter())
            touchTransmitterSet = true
        }

        val position = viewHolder.adapterPosition

        if (position == RecyclerView.NO_POSITION) return

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Careful, dX is not the delta of user's movement, it's the new offset of the swiped view's left side !
            val isLeftArea = dX < 0
            // Android's ItemTouchHelper incorrectly sets dX to recyclerView.width when swiping to the max,
            // which breaks the animation when itemView is smaller than that (e.g. two columns layout)
            // => fix animation by limiting dX to the itemView's width
            val dXPercent = dX / recyclerView.width

            // If unswiped, fire event and update swiped state
            if (0f == dX && swipedStates.containsKey(position)) {
                itemSwipeCallback?.itemUnswiped(viewHolder.adapterPosition)
                swipedStates.remove(position)
            }

            // If the position is between "swiped" and "unswiped", then we're swiping
            isSwiping = (abs(dXPercent) > 0 && abs(dXPercent) < 1)

            var swipeableView = itemView
            if (viewHolder is IDrawerSwipeableViewHolder) swipeableView = viewHolder.swipeableView

            val swipeWidthPc = recyclerView.context.resources.displayMetrics.density * if (isLeftArea) swipeWidthLeftDp else swipeWidthRightDp
            swipeableView.translationX = dXPercent * swipeWidthPc
        } else super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    /**
     * Hack to force-transmit click events to the first visible View at the clicked coordinates
     * [< swiped area ] exposed sublayer ]
     * Android default touch event mechanisms don't transmit these events to the sublayer :
     * any click on the exposed surface just swipes the item back to where it came
     */
    inner class RecyclerTouchTransmitter : View.OnTouchListener {

        override fun onTouch(v: View?, event: MotionEvent): Boolean {
            if (isSwiping || null == v || v !is ViewGroup) return false

            // Get the first visible View under the clicked coordinates
            val childView = v.getFirstVisibleViewByCoordinates(event.x, event.y)
            // Transmit the ACTION_DOWN and ACTION_UP events to this View
            if (childView != null)
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        println("> action down")
                        return childView.onTouchEvent(event)
                    }
                    MotionEvent.ACTION_UP -> {
                        println("> action up")
                        return childView.onTouchEvent(event)
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
