package com.mikepenz.fastadapter.utils

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItemAdapter
import com.mikepenz.fastadapter.drag.IExtendedDraggable

/**
 * Created by flisar on 30.09.2016.
 */
object DragDropUtil {
    /**
     * This functions binds the view's touch listener to start the drag via the touch helper…
     *
     * @param holder the view holder
     * @param holder the item
     */
    @JvmStatic fun bindDragHandle(holder: RecyclerView.ViewHolder, item: IExtendedDraggable<RecyclerView.ViewHolder>) {
        // if necessary, init the drag handle, which will start the drag when touched
        if (item.touchHelper != null) {
            item.getDragView(holder)?.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    if (item.isDraggable)
                        item.touchHelper!!.startDrag(holder)
                }
                false
            }
        }
    }

    /**
     * This functions handles the default drag and drop move event
     * It takes care to move all items one by one within the passed in positions
     *
     * @param itemAdapter the adapter
     * @param oldPosition the start position of the move
     * @param newPosition the end position of the move
     */
    @JvmStatic fun onMove(itemAdapter: IItemAdapter<*, *>, oldPosition: Int, newPosition: Int) {
        // necessary, because the positions passed to this function may be jumping in case of
        // that the recycler view is scrolled while holding an item outside of the recycler view
        if (oldPosition < newPosition) {
            for (i in oldPosition + 1..newPosition) {
                itemAdapter.move(i, i - 1)
            }
        } else {
            for (i in oldPosition - 1 downTo newPosition) {
                itemAdapter.move(i, i + 1)
            }
        }
    }
}
