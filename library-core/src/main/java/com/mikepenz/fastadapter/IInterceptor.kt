package com.mikepenz.fastadapter

/**
 * Created by mikepenz on 30.12.15.
 */
interface IInterceptor<Element, Item> {

    fun intercept(element: Element): Item?

    companion object {

        val DEFAULT: IInterceptor<IItem<*>, IItem<*>> = object : IInterceptor<IItem<*>, IItem<*>> {
            override fun intercept(element: IItem<*>): IItem<*>? {
                return element
            }
        }
    }
}
