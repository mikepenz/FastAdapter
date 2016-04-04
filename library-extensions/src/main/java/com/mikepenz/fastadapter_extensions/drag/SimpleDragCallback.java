package com.mikepenz.fastadapter_extensions.drag;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.mikepenz.fastadapter.IDraggable;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * based on the sample from
 * https://github.com/AleBarreto/DragRecyclerView
 */
public class SimpleDragCallback extends ItemTouchHelper.SimpleCallback {

    //our callback
    private ItemTouchCallback mCallbackItemTouch; // interface
    private boolean mIsDragEnabled = true;

    private int mDirections = UP_DOWN;

    public static final int ALL = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    public static final int UP_DOWN = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    public static final int LEFT_RIGHT = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

    @IntDef({ALL, UP_DOWN, LEFT_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Directions {
    }

    public SimpleDragCallback() {
        super(UP_DOWN, 0);
    }

    public SimpleDragCallback(@Directions int directions) {
        super(directions, 0);
        this.mDirections = directions;
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
        if (mCallbackItemTouch == null) {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            ItemAdapter itemAdapter = null;
            if (adapter instanceof FastItemAdapter) {
                itemAdapter = ((FastItemAdapter) adapter).getItemAdapter();
            } else if (adapter instanceof ItemAdapter) {
                itemAdapter = (ItemAdapter) adapter;
            }
            if (itemAdapter != null) {
                itemAdapter.move(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }
            throw new RuntimeException("SimpleDragCallback without an callback is only allowed when using the ItemAdapter or the FastItemAdapter");
        }
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