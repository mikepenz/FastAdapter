package com.mikepenz.fastadapter

/**
 * MutableList proxy which will properly set and remove the parent for items added/set/removed.
 * This is important as otherwise collapsing will not work properly (does not resolve parent relationships)
 */
class MutableSubItemList<E : ISubItem<*>>(val parent: IParentItem<*>, val list: MutableList<E> = mutableListOf()) : MutableList<E> by list {

    override fun remove(element: E): Boolean {
        return list.remove(element).also { removed -> if (removed) element.parent = null }
    }

    override fun removeAt(index: Int): E {
        return list.removeAt(index).also { element -> element.parent = null }
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        elements.filter { list.contains(it) }.forEach { it.parent = null }
        return list.removeAll(elements)
    }

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
        return list.set(index, element).also { oldElement -> oldElement.parent = null }
    }

    override fun clear() {
        list.forEach { it.parent = null }
        list.clear()
    }

    fun setNewList(newList: List<E>) {
        clear()
        addAll(newList)
    }
}