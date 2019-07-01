package com.mikepenz.fastadapter.utils

import com.mikepenz.fastadapter.GenericItem

/**
 * Created by mikepenz on 30.12.15.
 */
interface InterceptorUtil {
    companion object {
        @JvmField
        val DEFAULT: (element: GenericItem) -> GenericItem? = { it }
    }
}
