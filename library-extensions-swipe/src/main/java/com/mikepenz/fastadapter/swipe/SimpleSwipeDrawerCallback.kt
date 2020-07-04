package com.mikepenz.fastadapter.swipe

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem


/**
 * Created by Mattias on 2016-02-13.
 */
class SimpleSwipeDrawerCallback @JvmOverloads constructor(private val swipeDirs: Int = ItemTouchHelper.LEFT) : ItemTouchHelper.SimpleCallback(0, swipeDirs) {
    // Swipe movement control
    private var sensitivityFactor = 1f

    private var swipeWidthDp = 80

    private lateinit var recyclerTouchListener: OnItemTouchListener

    private var recyclerViewListened = false


    fun withSwipeLeft(): SimpleSwipeDrawerCallback {
        setDefaultSwipeDirs(swipeDirs or ItemTouchHelper.LEFT)
        return this
    }

    fun withSwipeRight(): SimpleSwipeDrawerCallback {
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
        // Not enabled
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

        if (!recyclerViewListened) {
            recyclerTouchListener = RecyclerItemClickListener(recyclerView.context, recyclerView, null)
            recyclerView.setOnTouchListener { v, event ->
                Log.i("aa", ">> recyclerView click " + event.x + " / " + event.y + " - " + event.actionMasked)
                // Get the viewholder's root element
                var childView = recyclerView.findChildViewUnder(event.x, event.y)
                if (childView != null && childView is ViewGroup) {
                    val position = recyclerView.layoutManager?.getPosition(childView)
                    Log.i("aa", ">> recyclerView pos $position")

                    // Get the ViewGroup that's been clicked on (upper or lower layer)
                    val childViewOffset = Rect()
                    childView.getHitRect(childViewOffset)
                    childView = childView.getInnermostViewByCoordinates(event.x - childViewOffset.left, event.y - childViewOffset.top)
                    if (childView != null)
                        when (event.actionMasked) {
                            MotionEvent.ACTION_DOWN -> {
                                childView.onTouchEvent(event)
                            }
                            MotionEvent.ACTION_UP -> {
                                childView.onTouchEvent(event)
                            }
                        }
                }
                false
            }
            recyclerViewListened = true
        }

        if (viewHolder.adapterPosition == RecyclerView.NO_POSITION) {
            return
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            // Click on the empty space that reveals the underlying buttons => do nothing; buttons should intercept the click
//            if (isCurrentlyActive && abs(dX).toInt() == itemView.width) return

            val swipeWidthPc = (recyclerView.context.resources.displayMetrics.density * swipeWidthDp) / itemView.width

            var swipeableView = itemView
            if (viewHolder is IDrawerSwipeable) swipeableView = viewHolder.swipeableView

            swipeableView.translationX = dX * swipeWidthPc
        } else super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    fun ViewGroup.getInnermostViewByCoordinates(x: Float, y: Float): View? {
        (childCount - 1 downTo 0)
                .map { this.getChildAt(it) }
                .forEach {
                    val bounds = Rect()
                    it.getHitRect(bounds)
                    if (bounds.contains(x.toInt(), y.toInt())) {
                        return if (it is ViewGroup) it.getInnermostViewByCoordinates(x - bounds.left, y - bounds.top)
                        else it
                    }
                }
        return null
    }


    class RecyclerItemClickListener(context: Context?, recyclerView: RecyclerView, private val mListener: OnItemClickListener?) : OnItemTouchListener {

        interface OnItemClickListener {
            fun onItemClick(view: View?, position: Int)
            fun onLongItemClick(view: View?, position: Int)
        }

        var mGestureDetector: GestureDetector
        override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
            val childView = view.findChildViewUnder(e.x, e.y)
            Log.i("aa", ">> RecyclerItemClickListener " + e.x + " / " + e.y + " - " + e.actionMasked)
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView))
                return true
            }
            return false
        }

        override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

        init {
            mGestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    val child = recyclerView.findChildViewUnder(e.x, e.y)
                    if (child != null && mListener != null) {
                        mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child))
                    }
                }
            })
        }
    }
}
