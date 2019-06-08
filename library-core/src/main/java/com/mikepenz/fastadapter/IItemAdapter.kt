package com.mikepenz.fastadapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by mikepenz on 30.12.15.
 */
interface IItemAdapter<Model, Item : IItem<out RecyclerView.ViewHolder>> : IAdapter<Item> {

    /**
     * set a new list of items and apply it to the existing list (clear - add) for this adapter
     *
     * @param items
     */
    fun set(items: List<Model>): IItemAdapter<Model, Item>

    /**
     * sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged
     *
     * @param items
     */
    fun setNewList(items: List<Model>): IItemAdapter<Model, Item>

    /**
     * add an array of items to the end of the existing items
     *
     * @param items
     */
    fun add(vararg items: Model): IItemAdapter<Model, Item>

    /**
     * add a list of items to the end of the existing items
     *
     * @param items
     */
    fun add(items: List<Model>): IItemAdapter<Model, Item>

    /**
     * add a list of items to the end of the existing items
     *
     * @param items
     */
    fun addInternal(items: List<Item>): IItemAdapter<Model, Item>

    /**
     * add an array of items at the given position within the existing items
     *
     * @param position the global position
     * @param items
     */
    fun add(position: Int, vararg items: Model): IItemAdapter<Model, Item>

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param items
     */
    fun add(position: Int, items: List<Model>): IItemAdapter<Model, Item>

    /**
     * add a list of items at the given position within the existing items
     *
     * @param position the global position
     * @param items
     */
    fun addInternal(position: Int, items: List<Item>): IItemAdapter<Model, Item>

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param item
     */
    operator fun set(position: Int, item: Model): IItemAdapter<Model, Item>

    /**
     * sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     * @param item
     */
    fun setInternal(position: Int, item: Item): IItemAdapter<Model, Item>

    /**
     * removes an item at the given position within the existing icons
     *
     * @param position the global position
     */
    fun remove(position: Int): IItemAdapter<Model, Item>

    /**
     * removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount
     */
    fun removeRange(position: Int, itemCount: Int): IItemAdapter<Model, Item>

    /**
     * removes all items of this adapter
     */
    fun clear(): IItemAdapter<Model, Item>
}
