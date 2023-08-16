package com.mikepenz.fastadapter_extensions.drag;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DragSelectionItemTouchListener extends LongPressItemTouchListener implements RecyclerView.OnItemTouchListener {
    private RecyclerView.ViewHolder mPreviousViewHolder;
    private Rect mHitRect = new Rect();
    private List<RecyclerView.ViewHolder> mRangeSelection = new ArrayList<>();


    public DragSelectionItemTouchListener(Context context, OnItemInteractionListener listener) {
        super(context, listener);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_POINTER_UP) {
            cancelPreviousSelection();
            return false;
        } else {
            onLongPressedEvent(rv, e);
        }
        return mViewHolderLongPressed != null;
    }

    private void cancelPreviousSelection() {
        mViewHolderLongPressed = null;
        mViewHolderInFocus = null;
        mPreviousViewHolder = null;
        mRangeSelection.clear();
    }

    private boolean onActionMove(RecyclerView rv, MotionEvent e) {
        if (isMotionEventInCurrentViewHolder(e) || mViewHolderLongPressed == null) {
            return false;
        }
        if (mViewHolderLongPressed != null && mPreviousViewHolder == null) {
            mPreviousViewHolder = mViewHolderLongPressed;
        }
        View childViewUnder = rv.findChildViewUnder(e.getX(), e.getY());
        if (childViewUnder == null) return false;
        RecyclerView.ViewHolder viewHolder = rv.getChildViewHolder(childViewUnder);
        if (mPreviousViewHolder == null && viewHolder != null && mViewHolderLongPressed != null && viewHolder.getAdapterPosition() != mViewHolderLongPressed.getAdapterPosition()) {
            dispatchOnViewHolderHovered(rv, viewHolder);
            return true;
        } else if (mPreviousViewHolder != null && viewHolder != null && viewHolder.getAdapterPosition() != mPreviousViewHolder.getAdapterPosition()) {
            dispatchOnViewHolderHovered(rv, viewHolder);
            return true;
        }
        return false;
    }


    private boolean isMotionEventInCurrentViewHolder(MotionEvent e) {
        if (mPreviousViewHolder != null) {
            mPreviousViewHolder.itemView.getHitRect(mHitRect);
            return mHitRect.contains((int) e.getX(), (int) e.getY());
        }
        return false;
    }

    private void dispatchOnViewHolderHovered(RecyclerView rv, RecyclerView.ViewHolder viewHolder) {
        if (!checkForSpanSelection(rv, viewHolder)) {
            if (mListener != null) {
                mListener.onViewHolderHovered(rv, viewHolder);
            }
        }
        mPreviousViewHolder = viewHolder;
    }

    private boolean checkForSpanSelection(RecyclerView rv, RecyclerView.ViewHolder viewHolder) {
        if (rv.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager.LayoutParams endSelectionParams = (GridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
            GridLayoutManager.LayoutParams startSelectionParams = (GridLayoutManager.LayoutParams) mPreviousViewHolder.itemView.getLayoutParams();
            if (endSelectionParams.getSpanIndex() != startSelectionParams.getSpanIndex()) {
                dispatchRangeSelection(rv, viewHolder);
                return true;
            }
        }
        return false;
    }

    private void dispatchRangeSelection(RecyclerView rv, RecyclerView.ViewHolder viewHolder) {
        if (mListener != null) {
            mRangeSelection.clear();
            int start = Math.min(mPreviousViewHolder.getAdapterPosition() + 1, viewHolder.getAdapterPosition());
            int end = Math.max(mPreviousViewHolder.getAdapterPosition() + 1, viewHolder.getAdapterPosition());
            for (int i = start; i <= end; i++) {
                mRangeSelection.add(rv.findViewHolderForAdapterPosition(i));
            }
            mListener.onMultipleViewHoldersSelected(rv, mRangeSelection);
        }
    }


    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP || e.getAction() == MotionEvent.ACTION_POINTER_UP) {
            cancelPreviousSelection();
        } else if (mViewHolderLongPressed != null) {
            onActionMove(rv, e);
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
