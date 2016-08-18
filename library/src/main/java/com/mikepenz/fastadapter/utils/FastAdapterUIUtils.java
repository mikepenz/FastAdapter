package com.mikepenz.fastadapter.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.mikepenz.fastadapter.R;

/**
 * Created by mikepenz on 28.12.15.
 */
public class FastAdapterUIUtils {

    /**
     * helper to get the system default selectable background inclusive an active state
     *
     * @param ctx            the context
     * @param selected_color the selected color
     * @param animate        true if you want to fade over the states (only animates if API newer than Build.VERSION_CODES.HONEYCOMB)
     * @return the StateListDrawable
     */
    public static StateListDrawable getSelectableBackground(Context ctx, @ColorInt int selected_color, boolean animate) {
        StateListDrawable states = new StateListDrawable();

        ColorDrawable clrActive = new ColorDrawable(selected_color);
        states.addState(new int[]{android.R.attr.state_selected}, clrActive);

        states.addState(new int[]{}, ContextCompat.getDrawable(ctx, getSelectableBackground(ctx)));
        //if possible we enable animating across states
        int duration = ctx.getResources().getInteger(android.R.integer.config_shortAnimTime);
        states.setEnterFadeDuration(duration);
        states.setExitFadeDuration(duration);
        return states;
    }

    /**
     * helper to get the system default selectable background inclusive an active and pressed state
     *
     * @param ctx            the context
     * @param selected_color the selected color
     * @param pressed_alpha  0-255
     * @param animate        true if you want to fade over the states (only animates if API newer than Build.VERSION_CODES.HONEYCOMB)
     * @return the StateListDrawable
     */
    public static StateListDrawable getSelectablePressedBackground(Context ctx, @ColorInt int selected_color, int pressed_alpha, boolean animate) {
        StateListDrawable states = getSelectableBackground(ctx, selected_color, animate);
        ColorDrawable clrPressed = new ColorDrawable(adjustAlpha(selected_color, pressed_alpha));
        states.addState(new int[]{android.R.attr.state_pressed}, clrPressed);
        return states;
    }

    /**
     * adjusts the alpha of a color
     *
     * @param color the color
     * @param alpha the alpha value we want to set 0-255
     * @return the adjusted color
     */
    public static int adjustAlpha(@ColorInt int color, int alpha) {
        return (alpha << 24) | (color & 0x00ffffff);
    }

    /**
     * helper to get the system default selectable background
     *
     * @param ctx
     * @return
     */
    public static int getSelectableBackground(Context ctx) {
        // If we're running on Honeycomb or newer, then we can use the Theme's
        // selectableItemBackground to ensure that the View has a pressed state
        TypedValue outValue = new TypedValue();
        //it is important here to not use the android.R because this wouldn't add the latest drawable
        ctx.getTheme().resolveAttribute(R.attr.selectableItemBackground, outValue, true);
        return outValue.resourceId;
    }
}
