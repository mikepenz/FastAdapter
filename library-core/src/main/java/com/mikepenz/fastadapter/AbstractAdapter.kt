package com.mikepenz.fastadapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by mikepenz on 27.12.15.
 */
abstract class AbstractAdapter<Item : IItem<out RecyclerView.ViewHolder>> : IAdapter<Item> {
    override var fastAdapter: FastAdapter<Item>? = null
    /**
     * returs the position of this Adapter in the FastAdapter
     *
     * @return the position of this Adapter in the FastAdapter
     */
    /**
     * sets the position of this Adapter in the FastAdapter
     * @param order the position of this Adapter in the FastAdapter
     */
    override var order = -1

    /**
     * internal mapper to remember and add possible types for the RecyclerView
     *
     * @param items
     */
    override fun mapPossibleTypes(items: Iterable<Item>?) {
        fastAdapter?.let { fastAdapter ->
            if (items != null) {
                for (item in items) {
                    fastAdapter.registerTypeInstance(item)
                }
            }
        }
    }
}
