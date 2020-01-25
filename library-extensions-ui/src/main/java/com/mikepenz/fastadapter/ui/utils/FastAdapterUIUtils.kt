package com.mikepenz.fastadapter.ui.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.*
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.mikepenz.fastadapter.R
import java.util.*

/**
 * Created by mikepenz on 28.12.15.
 */
object FastAdapterUIUtils {

    /**
     * Helper to get the system default selectable background inclusive an active state
     *
     * @param ctx            the context
     * @param selected_color the selected color
     * @param animate        true if you want to fade over the states (only animates if API newer than [Build.VERSION_CODES.HONEYCOMB])
     * @return the [StateListDrawable]
     */
    @JvmStatic
    fun getSelectableBackground(
            ctx: Context,
            @ColorInt selected_color: Int,
            animate: Boolean
    ): StateListDrawable {
        val states = StateListDrawable()

        val clrActive = ColorDrawable(selected_color)
        states.addState(intArrayOf(android.R.attr.state_selected), clrActive)

        states.addState(intArrayOf(), ContextCompat.getDrawable(ctx, getSelectableBackground(ctx)))

        //if possible we enable animating across states
        if (animate) {
            val duration = ctx.resources.getInteger(android.R.integer.config_shortAnimTime)
            states.setEnterFadeDuration(duration)
            states.setExitFadeDuration(duration)
        }

        return states
    }

    /**
     * Helper to get the system default selectable background inclusive an active and pressed state
     *
     * @param ctx            the context
     * @param selected_color the selected color
     * @param pressed_alpha  0-255
     * @param animate        true if you want to fade over the states (only animates if API newer than [Build.VERSION_CODES.HONEYCOMB])
     * @return the [StateListDrawable]
     */
    @JvmStatic
    fun getSelectablePressedBackground(
            ctx: Context,
            @ColorInt selected_color: Int,
            pressed_alpha: Int,
            animate: Boolean
    ): StateListDrawable {
        val states = getSelectableBackground(ctx, selected_color, animate)
        val clrPressed = ColorDrawable(adjustAlpha(selected_color, pressed_alpha))
        states.addState(intArrayOf(android.R.attr.state_pressed), clrPressed)
        return states
    }

    /**
     * Adjusts the alpha of a color
     *
     * @param color the color
     * @param alpha the alpha value we want to set 0-255
     * @return the adjusted color
     */
    @JvmStatic
    fun adjustAlpha(@ColorInt color: Int, alpha: Int): Int {
        return alpha shl 24 or (color and 0x00ffffff)
    }

    /** Helper to get the system default selectable background */
    @JvmStatic
    fun getSelectableBackground(ctx: Context): Int {
        // If we're running on Honeycomb or newer, then we can use the Theme's
        // selectableItemBackground to ensure that the View has a pressed state
        val outValue = TypedValue()
        //it is important here to not use the android.R because this wouldn't add the latest drawable
        ctx.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true)
        return outValue.resourceId
    }

    /**
     * Helper to create an ripple drawable with the given normal and pressed color
     *
     * @param normalColor  the normal color
     * @param pressedColor the pressed color
     * @param radius       the button radius
     * @return the ripple drawable
     */
    @JvmStatic
    fun getRippleDrawable(
            @ColorInt normalColor: Int,
            @ColorInt pressedColor: Int,
            radius: Int
    ): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RippleDrawable(ColorStateList.valueOf(pressedColor),
                    ColorDrawable(normalColor), getRippleMask(normalColor, radius))
        } else {
            getStateListDrawable(normalColor, pressedColor)
        }
    }

    /**
     * Helper to create an ripple mask with the given color and radius
     *
     * @param color  the color
     * @param radius the radius
     * @return the mask drawable
     */
    @JvmStatic
    private fun getRippleMask(color: Int, radius: Int): Drawable {
        val outerRadius = FloatArray(8)
        Arrays.fill(outerRadius, radius.toFloat())
        val r = RoundRectShape(outerRadius, null, null)
        val shapeDrawable = ShapeDrawable(r)
        shapeDrawable.paint.color = color
        return shapeDrawable
    }

    /**
     * Helper to create an [StateListDrawable] for the given normal and pressed color
     *
     * @param normalColor  the normal color
     * @param pressedColor the pressed color
     * @return the [StateListDrawable]
     */
    @JvmStatic
    private fun getStateListDrawable(
            normalColor: Int, pressedColor: Int): StateListDrawable {
        val states = StateListDrawable()
        states.addState(intArrayOf(android.R.attr.state_pressed),
                ColorDrawable(pressedColor))
        states.addState(intArrayOf(android.R.attr.state_focused),
                ColorDrawable(pressedColor))
        states.addState(intArrayOf(android.R.attr.state_activated),
                ColorDrawable(pressedColor))
        states.addState(intArrayOf(),
                ColorDrawable(normalColor))
        return states
    }

    /**
     * helper to create a stateListDrawable for the icon
     *
     * @param icon
     * @param selectedIcon
     * @return
     */
    fun getIconStateList(icon: Drawable, selectedIcon: Drawable): StateListDrawable {
        val iconStateListDrawable = StateListDrawable()
        iconStateListDrawable.addState(intArrayOf(android.R.attr.state_selected), selectedIcon)
        iconStateListDrawable.addState(intArrayOf(), icon)
        return iconStateListDrawable
    }
}
