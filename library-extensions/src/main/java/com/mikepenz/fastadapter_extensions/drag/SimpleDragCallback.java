package com.mikepenz.fastadapter_extensions.drag;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.mikepenz.fastadapter.IDraggable;

/**
 * based on the sample from
 * https://github.com/AleBarreto/DragRecyclerView
 */
public class SimpleDragCallback extends ItemTouchHelper.SimpleCallback {

    //our callback
    private final ItemTouchCallback mCallbackItemTouch; // interface
    private boolean mIsDragEnabled = true;

    private int mDirections = UP_DOWN;

    public static final int ALL = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    public static final int UP_DOWN = ItemTouchHelper.UP | ItemTouchHelper.DOWN;

    @IntDef({ALL, UP_DOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Directions {
    }

    public SimpleDragCallback(@Directions int directions, ItemTouchCallback itemTouchCallback) {
        super(directions, 0);
        this.mDirections = directions;
        this.mCallbackItemTouch = itemTouchCallback;
    }

    public SimpleDragCallback(ItemTouchCallback itemTouchCallback) {
        super(UP_DOWN, 0);
        this.mCallbackItemTouch = itemTouchCallback;
    }

    public void setIsDragEnabled(boolean mIsDragEnabled) {
        this.mIsDragEnabled = mIsDragEnabled;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return mIsDragEnabled;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return mCallbackItemTouch.itemTouchOnMove(viewHolder.getAdapterPosition(), target.getAdapterPosition()); // information to the interface
    }

    @Override
    public int getDragDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder.itemView.getTag() instanceof IDraggable) {
            if (((IDraggable) viewHolder.itemView.getTag()).isDraggable()) {
                return super.getDragDirs(recyclerView, viewHolder);
            } else {
                return 0;
            }
        } else {
            return mDirections;
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // swiped disabled
    }
}