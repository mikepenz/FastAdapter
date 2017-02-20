package com.mikepenz.fastadapter_extensions.swipe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.mikepenz.fastadapter_extensions.drag.ItemTouchCallback;
import com.mikepenz.fastadapter_extensions.drag.SimpleDragCallback;

/**
 * Created by Mattias on 2016-02-13.
 */
public class SimpleSwipeDragCallback extends SimpleDragCallback {

    private final SimpleSwipeCallback simpleSwipeCallback;

    public SimpleSwipeDragCallback(ItemTouchCallback itemTouchCallback, SimpleSwipeCallback.ItemSwipeCallback itemSwipeCallback, Drawable leaveBehindDrawable) {
        this(itemTouchCallback, itemSwipeCallback, leaveBehindDrawable, ItemTouchHelper.LEFT);
    }

    public SimpleSwipeDragCallback(ItemTouchCallback itemTouchCallback, SimpleSwipeCallback.ItemSwipeCallback itemSwipeCallback, Drawable leaveBehindDrawable, int swipeDirs) {
        this(itemTouchCallback, itemSwipeCallback, leaveBehindDrawable, swipeDirs, Color.RED);
    }

    public SimpleSwipeDragCallback(ItemTouchCallback itemTouchCallback, SimpleSwipeCallback.ItemSwipeCallback itemSwipeCallback, Drawable leaveBehindDrawable, int swipeDirs, @ColorInt int bgColor) {
        super(itemTouchCallback);
        setDefaultSwipeDirs(swipeDirs);
        simpleSwipeCallback = new SimpleSwipeCallback(itemSwipeCallback, leaveBehindDrawable, swipeDirs, bgColor);
    }

    public SimpleSwipeDragCallback withLeaveBehindSwipeLeft(Drawable d) {
        setDefaultSwipeDirs(super.getSwipeDirs(null, null) | ItemTouchHelper.LEFT);
        simpleSwipeCallback.withLeaveBehindSwipeLeft(d);
        return this;
    }

    public SimpleSwipeDragCallback withLeaveBehindSwipeRight(Drawable d) {
        setDefaultSwipeDirs(super.getSwipeDirs(null, null) | ItemTouchHelper.RIGHT);
        simpleSwipeCallback.withLeaveBehindSwipeRight(d);
        return this;
    }

    public SimpleSwipeDragCallback withHorizontalMarginDp(Context ctx, int dp) {
        simpleSwipeCallback.withHorizontalMarginDp(ctx, dp);
        return this;
    }

    public SimpleSwipeDragCallback withHorizontalMarginPx(int px) {
        simpleSwipeCallback.withHorizontalMarginPx(px);
        return this;
    }

    public SimpleSwipeDragCallback withBackgroundSwipeLeft(@ColorInt int bgColor) {
        simpleSwipeCallback.withBackgroundSwipeLeft(bgColor);
        return this;
    }

    public SimpleSwipeDragCallback withBackgroundSwipeRight(@ColorInt int bgColor) {
        simpleSwipeCallback.withBackgroundSwipeRight(bgColor);
        return this;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        simpleSwipeCallback.onSwiped(viewHolder, direction);
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return simpleSwipeCallback.getSwipeDirs(recyclerView, viewHolder);
    }


    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        simpleSwipeCallback.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        //Happen to know that our direct parent class doesn't (currently) draw anything...
        //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
