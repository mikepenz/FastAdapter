package com.mikepenz.fastadapter_extensions.utilities;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.mikepenz.fastadapter_extensions.IExtendedDraggable;

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
    public static <VH extends RecyclerView.ViewHolder> void bindDragHandle(final VH holder, final IExtendedDraggable<?, VH, ?> item) {
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
}
