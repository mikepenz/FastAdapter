package com.mikepenz.fastadapter_extensions.utilities;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter_extensions.drag.IExtendedDraggable;

/**
 * Created by flisar on 30.09.2016.
 */

public class DragDropUtil
{
    /**
     * this functions binds the view's touch listener to start the drag via the touch helper...
     *
     * @param holder the view holder
     * @param holder the item
     */
    public static void bindDragHandle(final RecyclerView.ViewHolder holder, final IExtendedDraggable item) {
        // if necessary, init the drag handle, which will start the drag when touched
        if (item.getTouchHelper() != null && item.getDragView(holder) != null) {
            item.getDragView(holder).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        if (item.isDraggable())
                            item.getTouchHelper().startDrag(holder);
                    }
                    return false;
                }
            });
        }
    }

    /*
     * This functions handles the default drag and drop move event
     * It takes care to move all items one by one within the passed in positions
     *
     * @param fastAdapter the adapter
     * @param oldPosition the start position of the move
     * @param newPosition the end position of the move
     */
    public static void onMove(ItemAdapter itemAdapter, int oldPosition, int newPosition) {
        // necessary, because the positions passed to this function may be jumping in case of that the recycler view is scrolled while holding an item outside of the recycler view
        if (oldPosition < newPosition) {
            for (int i = oldPosition + 1; i <= newPosition; i++) {
                itemAdapter.move(i, i - 1);
            }
        } else {
            for (int i = oldPosition - 1; i >= newPosition; i--) {
                itemAdapter.move(i, i + 1);
            }
        }
    }
}
