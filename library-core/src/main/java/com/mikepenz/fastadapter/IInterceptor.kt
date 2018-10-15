package com.mikepenz.fastadapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by mikepenz on 30.12.15.
 */
interface IInterceptor<Element, Item> {

    fun intercept(element: Element): Item?

    companion object {
        val DEFAULT: IInterceptor<IItem<out RecyclerView.ViewHolder>, IItem<out RecyclerView.ViewHolder>> =
            object :
                IInterceptor<IItem<out RecyclerView.ViewHolder>, IItem<out RecyclerView.ViewHolder>> {
                override fun intercept(element: IItem<out RecyclerView.ViewHolder>): IItem<out RecyclerView.ViewHolder>? {
                    return element
                }
            }
    }
}
