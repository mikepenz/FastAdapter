package com.mikepenz.fastadapter

/**
 * MutableList proxy which will properly set the parent for items added/set.
 * This is important as otherwise collapsing will not work properly (does not resolve parent relationships)
 */
class MutableSubItemList<E : ISubItem<*>>(val parent: IParentItem<*>, val list: MutableList<E> = mutableListOf()) : MutableList<E> by list {
    override fun add(element: E): Boolean {
        element.parent = parent
        return list.add(element)
    }

    override fun add(index: Int, element: E) {
        element.parent = parent
        return list.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        elements.forEach { it.parent = parent }
        return list.addAll(index, elements)
    }

    override fun addAll(elements: Collection<E>): Boolean {
        elements.forEach { it.parent = parent }
        return list.addAll(elements)
    }

    override fun set(index: Int, element: E): E {
        element.parent = parent
        return list.set(index, element)
    }
}