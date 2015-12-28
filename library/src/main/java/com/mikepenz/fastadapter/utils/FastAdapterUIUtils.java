package com.mikepenz.fastadapter.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

/**
 * Created by mikepenz on 28.12.15.
 */
public class FastAdapterUIUtils {

    /**
     * helper to create a StateListDrawable for the drawer item background
     *
     * @param selected_color
     * @return
     */
    public static StateListDrawable getItemBackground(int selected_color) {
        ColorDrawable clrActive = new ColorDrawable(selected_color);
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_selected}, clrActive);
        return states;
    }

    /**
     * helper to get the system default selectable background inclusive an active state
     *
     * @param ctx
     * @param selected_color
     * @return
     */
    public static StateListDrawable getSelectableBackground(Context ctx, int selected_color) {
        StateListDrawable states = getItemBackground(selected_color);
        states.addState(new int[]{}, ContextCompat.getDrawable(ctx, getSelectableBackground(ctx)));
        return states;
    }

    /**
     * helper to get the system default selectable background
     *
     * @param ctx
     * @return
     */
    public static int getSelectableBackground(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            TypedValue outValue = new TypedValue();
            ctx.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            return outValue.resourceId;
        } else {
            TypedValue outValue = new TypedValue();
            ctx.getTheme().resolveAttribute(android.R.attr.itemBackground, outValue, true);
            return outValue.resourceId;
        }
    }
}
