package com.mikepenz.fastadapter.utils

/**
 * @author pa.gulko zTrap (14.12.2019)
 */
internal fun <T> MutableList<T>.trySortWith(comparator: Comparator<in T>?) {
    comparator?.let { sortWith(it) }
}