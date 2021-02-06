package com.mikepenz.fastadapter

import android.util.Log

/**
 * @author pa.gulko zTrap (16.10.2019)
 */
internal class VerboseLogger(private val tag: String) {

    var isEnabled = false

    fun log(message: String) {
        if (isEnabled) {
            Log.v(tag, message)
        }
    }
}