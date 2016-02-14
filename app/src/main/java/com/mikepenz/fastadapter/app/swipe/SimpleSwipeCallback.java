package com.mikepenz.fastadapter.app.swipe;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;


/**
 * Created by Mattias on 2016-02-13.
 */
public class SimpleSwipeCallback extends ItemTouchHelper.SimpleCallback {

    public interface ItemSwipeCallback {

        /**
         * Called when an item has been swiped
         *
         * @param position  position of item in the adapter
         * @param direction direction the item was swiped
         * @return true if moved otherwise false
         */
        void itemSwiped(int position, int direction);

    }

    private final ItemSwipeCallback itemSwipeCallback;

    private final int bgColor;

    private Drawable leaveBehindDrawable;
    private Paint bgPaint;
    private int horizMargin;

    public SimpleSwipeCallback(SimpleSwipeCallback.ItemSwipeCallback itemSwipeCallback, Drawable leaveBehindDrawable) {
        this(itemSwipeCallback, leaveBehindDrawable, ItemTouchHelper.LEFT);
    }

    public SimpleSwipeCallback(SimpleSwipeCallback.ItemSwipeCallback itemSwipeCallback, Drawable leaveBehindDrawable, int swipeDirs) {
        this(itemSwipeCallback, leaveBehindDrawable, swipeDirs, Color.RED);
    }

    public SimpleSwipeCallback(ItemSwipeCallback itemSwipeCallback, Drawable leaveBehindDrawable, int swipeDirs, @ColorInt int bgColor) {
        super(0, swipeDirs);
        this.itemSwipeCallback = itemSwipeCallback;
        this.leaveBehindDrawable = leaveBehindDrawable;
        this.bgColor = bgColor;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            itemSwipeCallback.itemSwiped(position, direction);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // not enabled
        return false;
    }

    //Inspired/modified from: https://github.com/nemanja-kovacevic/recycler-view-swipe-to-delete/blob/master/app/src/main/java/net/nemanjakovacevic/recyclerviewswipetodelete/MainActivity.java
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        if (viewHolder.getAdapterPosition() == RecyclerView.NO_POSITION) {
            return;
        }
        if (Math.abs(dX) > Math.abs(dY)) {
            if (bgPaint == null) {
                bgPaint = new Paint();
                bgPaint.setColor(bgColor);
                horizMargin = (int)(16 * recyclerView.getResources().getDisplayMetrics().density);
            }

            if (bgColor != Color.TRANSPARENT) {
                c.drawRect(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom(), bgPaint);
            }

            if (leaveBehindDrawable != null) {
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = leaveBehindDrawable.getIntrinsicWidth();
                int intrinsicHeight = leaveBehindDrawable.getIntrinsicWidth();

                int left = itemView.getRight() - horizMargin - intrinsicWidth;
                int right = itemView.getRight() - horizMargin;
                int top = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int bottom = top + intrinsicHeight;
                leaveBehindDrawable.setBounds(left, top, right, bottom);

                leaveBehindDrawable.draw(c);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
