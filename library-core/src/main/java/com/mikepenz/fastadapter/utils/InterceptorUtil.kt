package com.mikepenz.fastadapter.utils

import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.IItem

/**
 * Created by mikepenz on 30.12.15.
 */
interface InterceptorUtil {
    companion object {
        val DEFAULT: (element: IItem<out RecyclerView.ViewHolder>) -> IItem<out RecyclerView.ViewHolder>? = {
            it
        }
    }
}
