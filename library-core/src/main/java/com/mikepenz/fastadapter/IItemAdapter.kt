package com.mikepenz.fastadapter

/**
 * Created by mikepenz on 30.12.15.
 */
interface IItemAdapter<Model, Item : GenericItem> : IAdapter<Item> {

    /** The [IIdDistributor] used to provide identifiers to added items (if no identifier was specified prior) */
    var idDistributor: IIdDistributor<Item>

    /** Set a new list of items and apply it to the existing list (clear — add) for this adapter */
    fun set(items: List<Model>): IItemAdapter<Model, Item>

    /** Sets a complete new list of items onto this adapter, using the new list. Calls notifyDataSetChanged */
    fun setNewList(items: List<Model>, retainFilter: Boolean = false): IItemAdapter<Model, Item>

    /** Add an array of items to the end of the existing items */
    fun add(vararg items: Model): IItemAdapter<Model, Item>

    /** Add a list of items to the end of the existing items */
    fun add(items: List<Model>): IItemAdapter<Model, Item>

    /** Add a list of items to the end of the existing items */
    fun addInternal(items: List<Item>): IItemAdapter<Model, Item>

    /**
     * Add an array of items at the given position within the existing items
     *
     * @param position the global position
     */
    fun add(position: Int, vararg items: Model): IItemAdapter<Model, Item>

    /**
     * Add a list of items at the given position within the existing items
     *
     * @param position the global position
     */
    fun add(position: Int, items: List<Model>): IItemAdapter<Model, Item>

    /**
     * Add a list of items at the given position within the existing items
     *
     * @param position the global position
     */
    fun addInternal(position: Int, items: List<Item>): IItemAdapter<Model, Item>

    /**
     * Sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     */
    operator fun set(position: Int, item: Model): IItemAdapter<Model, Item>

    /**
     * Sets an item at the given position, overwriting the previous item
     *
     * @param position the global position
     */
    fun setInternal(position: Int, item: Item): IItemAdapter<Model, Item>

    /**
     * Moves the item at the [fromPosition] to the [toPosition]
     */
    fun move(fromPosition: Int, toPosition: Int): IItemAdapter<Model, Item>

    /**
     * Removes an item at the given position within the existing icons
     *
     * @param position the global position
     */
    fun remove(position: Int): IItemAdapter<Model, Item>

    /**
     * Removes a range of items starting with the given position within the existing icons
     *
     * @param position  the global position
     * @param itemCount
     */
    fun removeRange(position: Int, itemCount: Int): IItemAdapter<Model, Item>

    /**
     * Removes all items of this adapter
     */
    fun clear(): IItemAdapter<Model, Item>

    /**
     * Filters the items with the constraint using the provided Predicate
     *
     * @param constraint the string used to filter the list
     */
    fun filter(constraint: CharSequence?)
}
