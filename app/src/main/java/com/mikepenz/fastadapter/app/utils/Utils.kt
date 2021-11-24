package com.mikepenz.fastadapter.app.utils

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.RestrictTo
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

internal fun Context.getThemeColor(attr: Int): Int {
    val tv = TypedValue()
    return if (this.theme.resolveAttribute(attr, tv, true)) {
        if (tv.resourceId != 0) {
            ContextCompat.getColor(this, tv.resourceId)
        } else {
            tv.data
        }
    } else {
        0
    }
}


/**
 * a helper method to get the color from an attribute
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun Context.getThemeColor(@AttrRes attr: Int, @ColorInt def: Int = 0): Int {
    val tv = TypedValue()
    return if (theme.resolveAttribute(attr, tv, true)) {
        if (tv.resourceId != 0) ResourcesCompat.getColor(resources, tv.resourceId, theme) else tv.data
    } else def
}