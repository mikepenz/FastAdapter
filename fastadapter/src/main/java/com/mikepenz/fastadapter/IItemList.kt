package com.mikepenz.fastadapter

/**
 * The Item list interface
 */

interface IItemList<Item : GenericItem> {

    var active: Boolean

    val isEmpty: Boolean

    val items: MutableList<Item>

    fun getAdapterPosition(identifier: Long): Int

    fun remove(position: Int, preItemCount: Int)

    fun removeRange(position: Int, itemCount: Int, preItemCount: Int)

    fun move(fromPosition: Int, toPosition: Int, preItemCount: Int)

    fun size(): Int

    fun clear(preItemCount: Int)

    fun addAll(items: List<Item>, preItemCount: Int)

    operator fun set(position: Int, item: Item, preItemCount: Int)

    operator fun set(items: List<Item>, preItemCount: Int, adapterNotifier: IAdapterNotifier?)

    fun setNewList(items: List<Item>, notify: Boolean)

    fun addAll(position: Int, items: List<Item>, preItemCount: Int)

    operator fun get(position: Int): Item?

    fun peek(position: Int): Item? {
        return get(position)
    }
}
